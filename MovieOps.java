import java.sql.*;


import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;


public class MovieOps {

    public static void rate(int rating, String movie, Connection conn) {

        //Rating has been checked for correctness, movie exists and user is logged in.

        String search = "SELECT uid FROM movies WHERE movie = ?";

        try (PreparedStatement stmt = conn.prepareStatement(search)) {
            stmt.setString(1, movie);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                int uid = resultset.getInt("uid");

                // Assuming you want to insert a rating for this movie if it exists
                String insertRating = "INSERT INTO ratings (uid, rating) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertRating)) {
                    insertStmt.setInt(1, uid);
                    insertStmt.setInt(2, rating);
                    insertStmt.executeUpdate();
                    System.out.println("Rating added successfully.");
                }

            } else {
                System.out.println("movie does not exist.");//?SHOULD NOT HAPPEN

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void search(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Search by: name, release date, cast members, studio, or genre. ");
        String input = (scanner.nextLine()).toLowerCase();
        String search = "";
        if (input.equals("name")) {
            //search by title
            System.out.println("Enter the movie name to search: ");
            input = scanner.nextLine();

            // Prompt user for sorting option
            System.out.println("Choose a column to sort by (movie name, studio, genre, release year): ");
            String sortBy = scanner.nextLine().toLowerCase().trim();

            // Prompt user for sorting order
            System.out.println("Choose sorting order (ascending/descending): ");
            String sortOrder = scanner.nextLine().toLowerCase().trim();

            String column;
            switch (sortBy) {
                case "movie name":
                    column = "m.title";
                    break;
                case "studio":
                    column = "ctr.firstname"; // Replace with your actual column for the studio if needed
                    break;
                case "genre":
                    column = "g.name"; // Ensure this matches your genre column in the database
                    break;
                case "release year":
                    column = "EXTRACT(YEAR FROM ro.date)";
                    break;
                default:
                    System.out.println("Invalid column. Sorting by movie name by default.");
                    column = "m.title";
            }
            String order = "ASC";
            if ("descending".equals(sortOrder)) {
                order = "DESC";
            }
            search = "SELECT " +
                    "    m.movieid, " +
                    "    m.title, " +
                    "    m.lengthinminutes, " +
                    "    m.mpaa, " +
                    "    c.movieid, " +
                    "    c.contributorid, " +
                    "    ctr.contributorid, " +
                    "    ctr.firstname, " +
                    "    ctr.lastname, " +
                    "    d.contributorid AS director_id, " +
                    "    r.number_of_stars, " +
                    "    g.genreid, " +
                    "    g.name, " +
                    "    p.platformid, " +
                    "    ro.date, " +
                    "    ro.platformid " +
                    "FROM movies AS m " +
                    "LEFT JOIN directs AS d ON m.movieid = d.movieid " +
                    "LEFT JOIN casts AS c ON m.movieid = c.movieid " +
                    "LEFT JOIN contributors AS ctr ON c.contributorid = ctr.contributorid " +
                    "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
                    "LEFT JOIN has_genre AS hg ON m.movieid = hg.movieid " +
                    "LEFT JOIN genre AS g ON hg.genreid = g.genreid " +  // Ensured the join matches your schema
                    "LEFT JOIN released_on AS ro ON m.movieid = ro.movieid " +
                    "LEFT JOIN platform AS p ON ro.platformid = p.platformid " +
                    "WHERE m.title = ? " +
                    "ORDER BY " + column + " " + order;
            try (PreparedStatement stmt = conn.prepareStatement(search)) {
                stmt.setString(1, input);
                ResultSet rs = stmt.executeQuery();

                String currentTitle = "";
                int length = 0;
                String mpaa = "";
                Date releaseDate = null;
                String director ="";
                Set<String> uniqueCastMembers = new HashSet<>();
                List<Integer> userRatings = new ArrayList<>();

                while (rs.next()) {
                    String title = rs.getString("title");

                    // If we're processing a new movie, print the details of the previous one
                    if (!title.equals(currentTitle) && !currentTitle.isEmpty()) {
                        // Print the details of the previous movie
                        System.out.println("Title: " + currentTitle);
                        System.out.println("Length: " + length + " minutes");
                        System.out.println("MPAA Rating: " + mpaa);
                        System.out.println("Director: " + director);
                        System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                        System.out.println("User Ratings: " + userRatings.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")));
                        System.out.println("Release Date: " + releaseDate);
                        System.out.println("------------");

                        // Reset the accumulators for the new movie
                        director = "";
                        uniqueCastMembers.clear();
                        userRatings.clear();
                    }

                    // Update the details for the current movie
                    currentTitle = title;
                    length = rs.getInt("lengthinminutes");
                    mpaa = rs.getString("mpaa");
                    releaseDate = rs.getDate("date");

                    // Append the director's name only if it's not already set
                    if (director.isEmpty() && rs.getString("director_id") != null) {
                        director = rs.getString("firstname") + " " + rs.getString("lastname");
                    }

                    // Accumulate cast members (excluding the director)
                    String castMember = rs.getString("firstname") + " " + rs.getString("lastname");
                    if (rs.getString("contributorid") != null && !castMember.equals(director)) {
                        uniqueCastMembers.add(castMember);
                    }

                    // Accumulate user ratings
                    int rating = rs.getInt("number_of_stars");
                    if (rating > 0) {
                        userRatings.add(rating);
                    }
                }

                // Print the last movie's details
                if (!currentTitle.isEmpty()) {
                    System.out.println("Title: " + currentTitle);
                    System.out.println("Length: " + length + " minutes");
                    System.out.println("MPAA Rating: " + mpaa);
                    System.out.println("Director: " + director);
                    System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                    System.out.println("User Ratings: " + userRatings.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (input.equals("release date")) {
            System.out.println("Enter the release date (YYYY-MM-DD): ");
            Date releaseDate = Date.valueOf(scanner.nextLine());

            System.out.println("Choose a column to sort by (movie name, studio, genre, release year): ");
            String sortBy = scanner.nextLine().toLowerCase().trim();

            // Prompt user for sorting order
            System.out.println("Choose sorting order (ascending/descending): ");
            String sortOrder = scanner.nextLine().toLowerCase().trim();

            String column;
            switch (sortBy) {
                case "movie name":
                    column = "m.title";
                    break;
                case "studio":
                    column = "ctr.firstname"; // Replace with your actual column for the studio if needed
                    break;
                case "genre":
                    column = "g.name"; // Ensure this matches your genre column in the database
                    break;
                case "release year":
                    column = "EXTRACT(YEAR FROM ro.date)";
                    break;
                default:
                    System.out.println("Invalid column. Sorting by movie name by default.");
                    column = "m.title";
            }
            String order = "ASC";
            if ("descending".equals(sortOrder)) {
                order = "DESC";
            }
            search = "SELECT " +
                    "    m.movieid, " +
                    "    m.title, " +
                    "    m.lengthinminutes, " +
                    "    m.mpaa, " +
                    "    c.movieid, " +
                    "    c.contributorid, " +
                    "    ctr.contributorid, " +
                    "    ctr.firstname, " +
                    "    ctr.lastname, " +
                    "    d.contributorid AS director_id, " +
                    "    r.number_of_stars, " +
                    "    g.genreid, " +
                    "    g.name, " +
                    "    p.platformid, " +
                    "    ro.date, " +
                    "    ro.platformid " +
                    "FROM movies AS m " +
                    "LEFT JOIN directs AS d ON m.movieid = d.movieid " +
                    "LEFT JOIN casts AS c ON m.movieid = c.movieid " +
                    "LEFT JOIN contributors AS ctr ON c.contributorid = ctr.contributorid " +
                    "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
                    "LEFT JOIN has_genre AS hg ON m.movieid = hg.movieid " +
                    "LEFT JOIN genre AS g ON hg.genreid = g.genreid " +  // Ensured the join matches your schema
                    "LEFT JOIN released_on AS ro ON m.movieid = ro.movieid " +
                    "LEFT JOIN platform AS p ON ro.platformid = p.platformid " +
                    "WHERE DATE(ro.date) = ? " +
                    "ORDER BY " + column + " " + order;
            try (PreparedStatement stmt = conn.prepareStatement(search)) {
                stmt.setDate(1, releaseDate);
                ResultSet rs = stmt.executeQuery();

                String currentTitle = "";
                int length = 0;
                String mpaa = "";
                String director ="";
                Set<String> uniqueCastMembers = new HashSet<>();
                List<Integer> userRatings = new ArrayList<>();

                while (rs.next()) {
                    String title = rs.getString("title");

                    // If we're processing a new movie, print the details of the previous one
                    if (!title.equals(currentTitle) && !currentTitle.isEmpty()) {
                        // Print the details of the previous movie
                        System.out.println("Title: " + currentTitle);
                        System.out.println("Length: " + length + " minutes");
                        System.out.println("MPAA Rating: " + mpaa);
                        System.out.println("Director: " + director);
                        System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                        System.out.println("User Ratings: " + userRatings.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")));
                        System.out.println("Release Date: " + releaseDate);
                        System.out.println("------------");

                        // Reset the accumulators for the new movie
                        director = "";
                        uniqueCastMembers.clear();
                        userRatings.clear();
                    }

                    // Update the details for the current movie
                    currentTitle = title;
                    length = rs.getInt("lengthinminutes");
                    mpaa = rs.getString("mpaa");
                    releaseDate = rs.getDate("date");

                    // Append the director's name only if it's not already set
                    if (director.isEmpty() && rs.getString("director_id") != null) {
                        director = rs.getString("firstname") + " " + rs.getString("lastname");
                    }

                    // Accumulate cast members (excluding the director)
                    String castMember = rs.getString("firstname") + " " + rs.getString("lastname");
                    if (rs.getString("contributorid") != null && !castMember.equals(director)) {
                        uniqueCastMembers.add(castMember);
                    }

                    // Accumulate user ratings
                    int rating = rs.getInt("number_of_stars");
                    if (rating > 0) {
                        userRatings.add(rating);
                    }
                }

                // Print the last movie's details
                if (!currentTitle.isEmpty()) {
                    System.out.println("Title: " + currentTitle);
                    System.out.println("Length: " + length + " minutes");
                    System.out.println("MPAA Rating: " + mpaa);
                    System.out.println("Director: " + director);
                    System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                    System.out.println("User Ratings: " + userRatings.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else if (input.equals("cast members")) {

            System.out.println("Enter movie cast member first name");
            String first = scanner.nextLine();

            System.out.println("Enter movie cast member last name");
            String last = scanner.nextLine();

            System.out.println("Choose a column to sort by (movie name, studio, genre, release year): ");
            String sortBy = scanner.nextLine().toLowerCase().trim();

            // Prompt user for sorting order
            System.out.println("Choose sorting order (ascending/descending): ");
            String sortOrder = scanner.nextLine().toLowerCase().trim();

            String column;
            switch (sortBy) {
                case "movie name":
                    column = "m.title";
                    break;
                case "studio":
                    column = "ctr.firstname"; // Replace with your actual column for the studio if needed
                    break;
                case "genre":
                    column = "g.name"; // Ensure this matches your genre column in the database
                    break;
                case "release year":
                    column = "EXTRACT(YEAR FROM ro.date)";
                    break;
                default:
                    System.out.println("Invalid column. Sorting by movie name by default.");
                    column = "m.title";
            }
            String order = "ASC";
            if ("descending".equals(sortOrder)) {
                order = "DESC";
            }
            search = "SELECT " +
                    "    m.movieid, " +
                    "    m.title, " +
                    "    m.lengthinminutes, " +
                    "    m.mpaa, " +
                    "    c.movieid, " +
                    "    c.contributorid, " +
                    "    ctr.contributorid, " +
                    "    ctr.firstname, " +
                    "    ctr.lastname, " +
                    "    d.contributorid AS director_id, " +
                    "    r.number_of_stars, " +
                    "    g.genreid, " +
                    "    g.name, " +
                    "    p.platformid, " +
                    "    ro.date, " +
                    "    ro.platformid " +
                    "FROM movies AS m " +
                    "LEFT JOIN directs AS d ON m.movieid = d.movieid " +
                    "LEFT JOIN casts AS c ON m.movieid = c.movieid " +
                    "LEFT JOIN contributors AS ctr ON c.contributorid = ctr.contributorid " +
                    "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
                    "LEFT JOIN has_genre AS hg ON m.movieid = hg.movieid " +
                    "LEFT JOIN genre AS g ON hg.genreid = g.genreid " +  // Ensured the join matches your schema
                    "LEFT JOIN released_on AS ro ON m.movieid = ro.movieid " +
                    "LEFT JOIN platform AS p ON ro.platformid = p.platformid " +
                    "WHERE ctr.firstname = ? AND ctr.lastname = ? " +
                    "ORDER BY " + column + " " + order;
            try (PreparedStatement stmt = conn.prepareStatement(search)) {
                stmt.setString(1, first);
                stmt.setString(2, last);
                ResultSet rs = stmt.executeQuery();

                String currentTitle = "";
                int length = 0;
                String mpaa = "";
                Date releaseDate = null;
                String director ="";
                Set<String> uniqueCastMembers = new HashSet<>();
                List<Integer> userRatings = new ArrayList<>();

                while (rs.next()) {
                    String title = rs.getString("title");

                    // If we're processing a new movie, print the details of the previous one
                    if (!title.equals(currentTitle) && !currentTitle.isEmpty()) {
                        // Print the details of the previous movie
                        System.out.println("Title: " + currentTitle);
                        System.out.println("Length: " + length + " minutes");
                        System.out.println("MPAA Rating: " + mpaa);
                        System.out.println("Director: " + director);
                        System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                        System.out.println("User Ratings: " + userRatings.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")));
                        System.out.println("Release Date: " + releaseDate);
                        System.out.println("------------");

                        // Reset the accumulators for the new movie
                        director = "";
                        uniqueCastMembers.clear();
                        userRatings.clear();
                    }

                    // Update the details for the current movie
                    currentTitle = title;
                    length = rs.getInt("lengthinminutes");
                    mpaa = rs.getString("mpaa");
                    releaseDate = rs.getDate("date");

                    // Append the director's name only if it's not already set
                    if (director.isEmpty() && rs.getString("director_id") != null) {
                        director = rs.getString("firstname") + " " + rs.getString("lastname");
                    }

                    // Accumulate cast members (excluding the director)
                    String castMember = rs.getString("firstname") + " " + rs.getString("lastname");
                    if (rs.getString("contributorid") != null && !castMember.equals(director)) {
                        uniqueCastMembers.add(castMember);
                    }

                    // Accumulate user ratings
                    int rating = rs.getInt("number_of_stars");
                    if (rating > 0) {
                        userRatings.add(rating);
                    }
                }

                // Print the last movie's details
                if (!currentTitle.isEmpty()) {
                    System.out.println("Title: " + currentTitle);
                    System.out.println("Length: " + length + " minutes");
                    System.out.println("MPAA Rating: " + mpaa);
                    System.out.println("Director: " + director);
                    System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                    System.out.println("User Ratings: " + userRatings.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (input.equals("studio")) {
            System.out.println("Enter movie studio name");
            String studio = scanner.nextLine();

            System.out.println("Choose a column to sort by (movie name, studio, genre, release year): ");
            String sortBy = scanner.nextLine().toLowerCase().trim();

            // Prompt user for sorting order
            System.out.println("Choose sorting order (ascending/descending): ");
            String sortOrder = scanner.nextLine().toLowerCase().trim();

            String column;
            switch (sortBy) {
                case "movie name":
                    column = "m.title";
                    break;
                case "studio":
                    column = "ctr.firstname"; // Replace with your actual column for the studio if needed
                    break;
                case "genre":
                    column = "g.name"; // Ensure this matches your genre column in the database
                    break;
                case "release year":
                    column = "EXTRACT(YEAR FROM ro.date)";
                    break;
                default:
                    System.out.println("Invalid column. Sorting by movie name by default.");
                    column = "m.title";
            }
            String order = "ASC";
            if ("descending".equals(sortOrder)) {
                order = "DESC";
            }
            search = "SELECT " +
                    "    m.movieid, " +
                    "    m.title, " +
                    "    m.lengthinminutes, " +
                    "    m.mpaa, " +
                    "    c.movieid, " +
                    "    c.contributorid, " +
                    "    ctr.contributorid, " +
                    "    ctr.firstname, " +
                    "    ctr.lastname, " +
                    "    d.contributorid AS director_id, " +
                    "    r.number_of_stars, " +
                    "    g.genreid, " +
                    "    g.name, " +
                    "    p.platformid, " +
                    "    ro.date, " +
                    "    ro.platformid " +
                    "FROM movies AS m " +
                    "LEFT JOIN directs AS d ON m.movieid = d.movieid " +
                    "LEFT JOIN casts AS c ON m.movieid = c.movieid " +
                    "LEFT JOIN contributors AS ctr ON c.contributorid = ctr.contributorid " +
                    "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
                    "LEFT JOIN has_genre AS hg ON m.movieid = hg.movieid " +
                    "LEFT JOIN genre AS g ON hg.genreid = g.genreid " +  // Ensured the join matches your schema
                    "LEFT JOIN released_on AS ro ON m.movieid = ro.movieid " +
                    "LEFT JOIN platform AS p ON ro.platformid = p.platformid " +
                    "WHERE ctr.firstname = ? AND ctr.lastname IS NULL " +
                    "ORDER BY " + column + " " + order;
            try (PreparedStatement stmt = conn.prepareStatement(search)) {
                stmt.setString(1, studio);
                ResultSet rs = stmt.executeQuery();

                String currentTitle = "";
                int length = 0;
                String mpaa = "";
                Date releaseDate = null;
                String director ="";
                Set<String> uniqueCastMembers = new HashSet<>();
                List<Integer> userRatings = new ArrayList<>();

                while (rs.next()) {
                    String title = rs.getString("title");

                    // If we're processing a new movie, print the details of the previous one
                    if (!title.equals(currentTitle) && !currentTitle.isEmpty()) {
                        // Print the details of the previous movie
                        System.out.println("Title: " + currentTitle);
                        System.out.println("Length: " + length + " minutes");
                        System.out.println("MPAA Rating: " + mpaa);
                        System.out.println("Director: " + director);
                        System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                        System.out.println("User Ratings: " + userRatings.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")));
                        System.out.println("Release Date: " + releaseDate);
                        System.out.println("------------");

                        // Reset the accumulators for the new movie
                        director = "";
                        uniqueCastMembers.clear();
                        userRatings.clear();
                    }

                    // Update the details for the current movie
                    currentTitle = title;
                    length = rs.getInt("lengthinminutes");
                    mpaa = rs.getString("mpaa");
                    releaseDate = rs.getDate("date");

                    // Append the director's name only if it's not already set
                    if (director.isEmpty() && rs.getString("director_id") != null) {
                        director = rs.getString("firstname") + " " + rs.getString("lastname");
                    }

                    // Accumulate cast members (excluding the director)
                    String castMember = rs.getString("firstname") + " " + rs.getString("lastname");
                    if (rs.getString("contributorid") != null && !castMember.equals(director)) {
                        uniqueCastMembers.add(castMember);
                    }

                    // Accumulate user ratings
                    int rating = rs.getInt("number_of_stars");
                    if (rating > 0) {
                        userRatings.add(rating);
                    }
                }

                // Print the last movie's details
                if (!currentTitle.isEmpty()) {
                    System.out.println("Title: " + currentTitle);
                    System.out.println("Length: " + length + " minutes");
                    System.out.println("MPAA Rating: " + mpaa);
                    System.out.println("Director: " + director);
                    System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                    System.out.println("User Ratings: " + userRatings.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (input.equals("genre")) {
            System.out.println("Enter the genre name");
            String genre = scanner.nextLine();

            System.out.println("Choose a column to sort by (movie name, studio, genre, release year): ");
            String sortBy = scanner.nextLine().toLowerCase().trim();

            // Prompt user for sorting order
            System.out.println("Choose sorting order (ascending/descending): ");
            String sortOrder = scanner.nextLine().toLowerCase().trim();

            String column;
            switch (sortBy) {
                case "movie name":
                    column = "m.title";
                    break;
                case "studio":
                    column = "ctr.firstname"; // Replace with your actual column for the studio if needed
                    break;
                case "genre":
                    column = "g.name"; // Ensure this matches your genre column in the database
                    break;
                case "release year":
                    column = "EXTRACT(YEAR FROM ro.date)";
                    break;
                default:
                    System.out.println("Invalid column. Sorting by movie name by default.");
                    column = "m.title";
            }
            String order = "ASC";
            if ("descending".equals(sortOrder)) {
                order = "DESC";
            }
            search = "SELECT " +
                    "    m.movieid, " +
                    "    m.title, " +
                    "    m.lengthinminutes, " +
                    "    m.mpaa, " +
                    "    c.movieid, " +
                    "    c.contributorid, " +
                    "    ctr.contributorid, " +
                    "    ctr.firstname, " +
                    "    ctr.lastname, " +
                    "    d.contributorid AS director_id, " +
                    "    r.number_of_stars, " +
                    "    g.genreid, " +
                    "    g.name, " +
                    "    p.platformid, " +
                    "    ro.date, " +
                    "    ro.platformid " +
                    "FROM movies AS m " +
                    "LEFT JOIN directs AS d ON m.movieid = d.movieid " +
                    "LEFT JOIN casts AS c ON m.movieid = c.movieid " +
                    "LEFT JOIN contributors AS ctr ON c.contributorid = ctr.contributorid " +
                    "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
                    "LEFT JOIN has_genre AS hg ON m.movieid = hg.movieid " +
                    "LEFT JOIN genre AS g ON hg.genreid = g.genreid " +  // Ensured the join matches your schema
                    "LEFT JOIN released_on AS ro ON m.movieid = ro.movieid " +
                    "LEFT JOIN platform AS p ON ro.platformid = p.platformid " +
                    "WHERE g.name = ?" +
                    "ORDER BY " + column + " " + order;
            try (PreparedStatement stmt = conn.prepareStatement(search)) {
                stmt.setString(1, genre);
                ResultSet rs = stmt.executeQuery();

                String currentTitle = "";
                int length = 0;
                String mpaa = "";
                Date releaseDate = null;
                String director ="";
                Set<String> uniqueCastMembers = new HashSet<>();
                List<Integer> userRatings = new ArrayList<>();

                while (rs.next()) {
                    String title = rs.getString("title");

                    // If we're processing a new movie, print the details of the previous one
                    if (!title.equals(currentTitle) && !currentTitle.isEmpty()) {
                        // Print the details of the previous movie
                        System.out.println("Title: " + currentTitle);
                        System.out.println("Length: " + length + " minutes");
                        System.out.println("MPAA Rating: " + mpaa);
                        System.out.println("Director: " + director);
                        System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                        System.out.println("User Ratings: " + userRatings.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")));
                        System.out.println("Release Date: " + releaseDate);
                        System.out.println("------------");

                        // Reset the accumulators for the new movie
                        director = "";
                        uniqueCastMembers.clear();
                        userRatings.clear();
                    }

                    // Update the details for the current movie
                    currentTitle = title;
                    length = rs.getInt("lengthinminutes");
                    mpaa = rs.getString("mpaa");
                    releaseDate = rs.getDate("date");

                    // Append the director's name only if it's not already set
                    if (director.isEmpty() && rs.getString("director_id") != null) {
                        director = rs.getString("firstname") + " " + rs.getString("lastname");
                    }

                    // Accumulate cast members (excluding the director)
                    String castMember = rs.getString("firstname") + " " + rs.getString("lastname");
                    if (rs.getString("contributorid") != null && !castMember.equals(director)) {
                        uniqueCastMembers.add(castMember);
                    }

                    // Accumulate user ratings
                    int rating = rs.getInt("number_of_stars");
                    if (rating > 0) {
                        userRatings.add(rating);
                    }
                }

                // Print the last movie's details
                if (!currentTitle.isEmpty()) {
                    System.out.println("Title: " + currentTitle);
                    System.out.println("Length: " + length + " minutes");
                    System.out.println("MPAA Rating: " + mpaa);
                    System.out.println("Director: " + director);
                    System.out.println("Cast Members: " + String.join(", ", uniqueCastMembers));
                    System.out.println("User Ratings: " + userRatings.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")));
                    System.out.println("Release Date: " + releaseDate);
                    System.out.println("------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else{
            System.out.println("Invalid input");
        }
        return;
}

    public static void watch(String movie,Connection conn, int userID) {

         String search = "SELECT MovieID FROM movies WHERE movie = ?";
        try (PreparedStatement stmt = conn.prepareStatement(search)){
            stmt.setString(1, movie);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()){
                int movieID = resultset.getInt("MovieID");
                String insertWatched = "INSERT INTO watched_movies (user_id, movie_id) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertWatched)) {
                        insertStmt.setInt(1, userID);
                        insertStmt.setInt(2, movieID);
                        insertStmt.executeUpdate();
                        System.out.println("Movie added to watched list.");
            }

                }else{
                    System.out.println("Movie does not exist.");

                }

        } catch (SQLException e){
            e.printStackTrace();
        }
        //movie exists, user is logged in
    }
}


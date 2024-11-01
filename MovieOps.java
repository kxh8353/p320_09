import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

public class MovieOps {

    public static void rate(int rating, String movie, Connection conn){
        //Rating has been checked for correctness, movie exists and user is logged in.

    }
    public static void search(Connection conn){
        Scanner sc = new Scanner(System.in);
        System.out.println("Would you like to search by: name, release date, cast members, studio, or genre? ");
        String input = sc.nextLine();
        input.toLowerCase(Locale.ROOT);
        if(input.equals("name")){
            System.out.println("Enter the movie name: ");
            input = sc.nextLine();
            String Titlequery = "SELECT " +
                    "    movies.movieid, " +
                    "    movies.title, " +
                    "    movies.lengthinminutes, " +
                    "    movies.mpaa, " +
                    "    casts.movieid, " +
                    "    casts.contributorid, " +
                    "    contributors.contributorid, " +
                    "    contributors.firstname, " +
                    "    contributors.lastname, " +
                    "    directs.contributorid, " +
                    "    rates.number_of_stars, " +
                    "    genres.genreid, " +
                    "    genres.name, " +
                    "    platforms.platformid, " +
                    "    released_on.date, " +
                    "    released_on.platformid " +
                    "FROM movies " +
                    "LEFT JOIN directs ON movies.movieid = directs.movieid " +
                    "LEFT JOIN casts ON movies.movieid = casts.movieid " +
                    "LEFT JOIN contributors ON casts.contributorid = contributors.contributorid " +
                    "LEFT JOIN rates ON movies.movieid = rates.movieid " +
                    "LEFT JOIN has_genre ON movies.movieid = has_genre.movieid " +
                    "LEFT JOIN genre ON has_genre.genreid = genre.genreid " +
                    "LEFT JOIN released_on ON movies.movieid = released_on.movieid " +
                    "LEFT JOIN platform ON released_on.platformid = platform.platformid " +
                    "WHERE movies.title = ?";
            try (PreparedStatement checktitlequery = conn.prepareStatement(Titlequery)){
                checktitlequery.setString(1, input);
                ResultSet rs = checktitlequery.executeQuery();
                if (!rs.isBeforeFirst()) { // Checks if the result set is empty
                    System.out.println("No movies found with the title: " + input);
                }
                else{
                    String title = rs.getString("title");
                    int length = rs.getInt("lengthinminutes");
                    String MPAA = rs.getString("mpaa");
                    System.out.println("Title: " + title + " Length: " + length + " MPAA: " + MPAA);
                    while (rs.next()) {
                        String contributors = rs.getString("firstname")+ " " + rs.getString("lastname");
                        System.out.println("Contributors: " + contributors);
                    }
                    int directorID = Integer.parseInt(rs.getString("contributorid"));
                    String directorName = rs.getString("firstname")+ " " + rs.getString("lastname");
                    System.out.println("Director: " + directorName + " " + directorID);
                    while (rs.next()) {
                        int rating = rs.getInt("number_of_stars");
                        System.out.println("Rating: " + rating);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else if(input.equals("cast")){
            System.out.println("Enter the cast member: ");
            input = sc.nextLine();
        }
        else if(input.equals("studio")){
            System.out.println("Enter the studio name: ");
            input = sc.nextLine();
        }
        else if(input.equals("genre")){
            System.out.println("Enter the genre: ");
            input = sc.nextLine();
        }
        else if(input.equals("release date")){
            System.out.println("Enter the release date: ");
            input = sc.nextLine();
        }
        else{
            System.out.println("Invalid input");
            return;
        }

    }

    public static void watch(String movie) {
        //movie exists, user is logged in
    }
}

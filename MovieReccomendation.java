import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MovieReccomendation {

    public static void Top20in90(Connection conn){
        String query = "SELECT " +
                "    m.movieid, " +
                "    m.title, " +
                "    COUNT(w.movieid) AS view_count " +
                "FROM movies AS m " +
                "JOIN watched AS w ON m.movieid = w.movieid " +
                "WHERE start_time >= CURRENT_DATE - INTERVAL '90 days' " +
                "GROUP BY m.movieid, m.title " +
                "ORDER BY view_count DESC " +
                "LIMIT 20";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top 20 most popular movies in the last 90 days:");
            while (rs.next()){
                String title = rs.getString("title");
                int viewCount = rs.getInt("view_count");
                System.out.println("Title: " + title + ", Views: " + viewCount);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public static void Top20moviesAmongFollowers(Connection conn, int uid){

        String query = "SELECT  " +
        "    m.movieid, " +
        "    m.title, " +
        "    COUNT(w.movieid) AS view_count " +
        "FROM movies AS m " +
        "JOIN watched AS w ON m.movieid = w.movieid " +
        "JOIN follows AS f ON w.uid = f.followee " +
        "WHERE f.follower = ? " +
        "GROUP BY m.movieid, m.title " +
        "ORDER BY view_count DESC "  +
        "LIMIT 20";

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top 20 most popular movies among the followers:");

            System.out.println("Executing query for user with UID: " + uid);

            if (!rs.isBeforeFirst()) {  
                System.out.println("No movies found for the followers.");
            }

            while (rs.next()){
                String title = rs.getString("title");
                int viewCount = rs.getInt("view_count");
                System.out.println("Title: " + title + ", Views: " + viewCount);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
        

    // For you: Recommend movies to watch to based on your play history
    // (e.g. genre, cast member, rating) and the play history of similar users
    public static void RecommendedMoviesForYou(Connection conn, int uid) {
        String query = "SELECT " +
        "   m.movieid, " +
        "   m.title, " +
        "   AVG(r.number_of_stars) AS avg_rating" +
        "FROM movies m " +
        "LEFT JOIN has_genre hg ON m.movieid = hg.movieid " +
        "LEFT JOIN genre g ON hg.genreid = g.genreid " +
        "LEFT JOIN rates r ON m.movieid = r.movieid " +
        "WHERE hg.genreid IN ( " +
            "SELECT g.genreid " +
            "FROM watched w " +
            "JOIN has_genre hg ON w.movieid = hg.movieid " +
            "JOIN genre g ON hg.genreid = g.genreid " +
            "WHERE w.uid = ? " +
            "GROUP BY g.genreid " +
        ") " +
        "GROUP BY m.movieid, m.title " +
        "UNION " +
        "SELECT " +
        "   m.movieid " +
        "   m.title " +
        "   AVG(r.number_of_stars) AS avg_rating" +
        "FROM movies m " +
        "LEFT JOIN casts AS cs ON m.movieid = cs.movieid " +
        "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
        "WHERE cs.contributorid IN ( "  +
            "SELECT c.contributorid " +
            "FROM watched w " +
            "JOIN casts cs ON w.movieid = cs.movieid " + 
            "JOIN contributors c ON cs.contributorid = c.contributorid " + 
            "JOIN has_genre hg ON w.movieid = hg.movieid " +
            "GROUP BY c.contributorid " +
        ") " +
        "GROUP BY m.movieid, m.title " +
        "UNION " +
        "SELECT "  +
        "   m.movieid, " +
        "   m.title, " +
        "   AVG(r.number_of_stars) AS avg_rating "  +
        "FROM movies m " +
        "LEFT JOIN rates AS r ON m.movieid = r.movieid " +
        "WHERE m.movieid IN ( " +
            "SELECT m.movieid " + 
            "FROM watched w " + 
            "JOIN movies m ON w.movieid = m.movieid " + 
            "LEFT JOIN rates r ON m.movieid = r.movieid " + 
            "WHERE w.uid = ? " + 
            "GROUP BY m.movieid, m.title " + 
            "ORDER BY AVG(r.number_of_stars) DESC " + 
            "LIMIT 10 " +
        ") " +
        "GROUP BY m.movieid, m.title " + 
        // "UNION " + 
        // "SELECT " +
        // "   m.movieid, " +
        // "   m.title, " +
        // "   AVG(r.number_of_stars) AS avg_rating " + 
        // "FROM movies m " + 
        // "JOIN watched w ON m.movieid = w.movieid" + 
        // "LEFT JOIN rates r ON m.movieid = r.movieid" + 
        // "WHERE w.uid in ( " + 
        //     "SELECT w2.uid " + 
        //     "FROM watched w " + 
        //     "JOIN watched w2 ON w.movieid = w2.movieid " + 
        //     "WHERE w.uid = ? " + 
        //     "GROUP BY w2.uid " + 
        // ") " + 
        // "GROUP BY m.movieid, m.title " +
        "ORDER BY avg_rating DESC " + 
        "LIMIT 10 ";
        

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, uid);
            stmt.setInt(2, uid);

            ResultSet rs = stmt.executeQuery();
            System.out.println("Top 10 Movies Recommended For You: ");
            while (rs.next()) {
                String title = rs.getString("title");
                double avgRating = rs.getDouble("avg_rating");
                System.out.println("Title: " + title + ", Average rating: " + avgRating);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void top5ThisMonth(Connection conn) {

        String top5Query = "Select m.title, m.mpaa, COUNT(w.movieid) AS watch_count\n" + //
                        "from movies as m\n" + //
                        "LEFT JOIN watched as w on m.movieid = w.movieid\n" + //
                        "LEFT JOIN released_on as r on m.movieid = r.movieid\n" + //
                        "\n" + //
                        "\n" + //
                        "WHERE EXTRACT(MONTH FROM r.date) = EXTRACT(MONTH FROM CURRENT_DATE)\n" + //
                        " AND\n" + //
                        "     EXTRACT(YEAR FROM r.date) = EXTRACT(YEAR FROM CURRENT_DATE)\n" + //
                        "   AND\n" + //
                        "     EXTRACT(MONTH FROM w.start_time) = EXTRACT(MONTH FROM CURRENT_DATE)\n" + //
                        " AND\n" + //
                        "     EXTRACT(YEAR FROM w.start_time) = EXTRACT(YEAR FROM CURRENT_DATE)\n" + //
                        "\n" + //
                        "\n" + //
                        "GROUP BY m.title, m.mpaa, m.movieid\n" + //
                        "ORDER BY watch_count DESC\n" + //
                        "LIMIT 5;";

        try (PreparedStatement topFivestatement = conn.prepareStatement(top5Query)){

            ResultSet rset = topFivestatement.executeQuery();
            int rows = 0;

            while(rset.next()) {   // Move the cursor to the next row
                String movieTitle = rset.getString("title");
                String rating = rset.getString("mpaa");
                if(rows==0){
                    System.out.println("Top Releases This Month!");
                }
                System.out.println((rows+1) + ". " + movieTitle + ", " + rating.toUpperCase());
                rows++;
            }

            if(rows == 0){
                System.out.println("No movies have both been released this month and recently watched");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    
}

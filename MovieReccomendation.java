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
    public static void RecommendedMoviesForYou(Connection conn, int uid){
        String query =
                "SELECT m.movieid, m.title, AVG(r.number_of_stars) AS avg_rating " +
                "FROM movies AS m " +
                "JOIN genre AS g ON m.movieid = g.genreid " +
                "JOIN casts AS c ON m.movieid = c.movieid " +
                "JOIN rates AS r ON m.movieid = r.movieid " +
                "WHERE g.genreid IN ( " +
                    "SELECT g2.genreid FROM genre AS g2 " +
                    "JOIN movies AS m2 ON g2.genreid = m2.movieid " +
                    "JOIN rates AS r2 ON m2.movieid = r2.movieid " +
                    "WHERE r2.uid = ? " +
                ") " +
                "AND c.contributorid IN ( " +
                    "SELECT c2.contributorid FROM casts AS c2 " +
                    "JOIN movies AS m2 ON c2.movieid = m2.movieid " +
                    "JOIN rates AS r2 ON m2.movieid = r2.movieid " +
                    "WHERE r2.uid = ? " +
                ") " +
                "GROUP BY m.movieid, m.title " +
                "ORDER BY avg_rating DESC " +
                "LIMIT 10";

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, uid);
            stmt.setInt(2, uid);

            ResultSet rs = stmt.executeQuery();

            System.out.println("Top 10 Movies Recommended For You: ");
            while (rs.next()){
                String title = rs.getString("title");
                double avgRating = rs.getDouble("avg_rating");
                System.out.println("Title: " + title + ", Average Rating: " + avgRating);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void top5ThisMonth(int uid, Connection conn) {

        LocalDateTime currentTime = LocalDateTime.now();
        

    }
    
}

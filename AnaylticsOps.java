import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnaylticsOps {

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

}

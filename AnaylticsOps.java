import java.sql.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AnaylticsOps {

    public static void displayProfile(Connection conn, int currentUser){
        CollectionCount(conn, currentUser);
        FollowersCount(conn, currentUser);
        FollowingCount(conn, currentUser);
        Top10Movies(conn, currentUser);
    }

    public static void CollectionCount(Connection conn, int currentUser){
        String Query = "SELECT COUNT(name) as total FROM collections WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("total");
                System.out.println("Number of Collections: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void FollowersCount(Connection conn, int currentUser){
        //KC - Please check for correctness 
        String Query = "SELECT COUNT(f.followee) AS follower_count\n" + //
                        "FROM users AS u\n" + //
                        "LEFT JOIN follows AS f ON u.uid = f.followee\n" + //
                        "WHERE u.uid = ?\n" + //
                        "";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("follower_count");
                System.out.println("Number of Users Following you: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void FollowingCount(Connection conn, int currentUser){
        //KC - Please check for correctness 
        String Query = "SELECT COUNT(f.follower) AS following_count\n" + //
                        "FROM users AS u\n" + //
                        "LEFT JOIN follows AS f ON u.uid = f.follower\n" + //
                        "WHERE u.uid = ?\n" + //
                        "";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("following_count");
                System.out.println("Number of Users you are Following: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void Top10Movies(Connection conn, int currentUser){
        //TODO
    }
}

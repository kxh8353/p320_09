import java.sql.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AnaylticsOps {
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
        //TODO
    }
    public static void FollowingCount(Connection conn, int currentUser){
        //TODO
    }
    public static void Top10Movies(Connection conn, int currentUser){
        //TODO
    }
}

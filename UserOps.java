import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class UserOps {

    // authenticate user with the database

    public static void followUsers(Connection conn, String toBeFollowedUser){

        ///first, parse list of usernames and IDs and see if username exists
        //if not, error handling needed

        String query = "SELECT COUNT(*) FROM users WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            int idtostring = Integer.parseInt(toBeFollowedUser);
            stmt.setInt(1, idtostring);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next() && rs.getInt(1) == 0){
                    System.out.println("User with ID " + toBeFollowedUser + " does not exist.");
                    return;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return;
        }

        System.out.println("Enter your userID:");
        Scanner scanner = new Scanner(System.in);
        String thisuser = scanner.nextLine();


        String followQuery = "INSERT INTO follows (Follower, Followee) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(followQuery)) {
            stmt.setInt(1, Integer.parseInt(thisuser));
            stmt.setInt(2, Integer.parseInt(toBeFollowedUser));
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(thisuser + " is now following " + toBeFollowedUser);
            } else {
                System.out.println("Follow operation failed. The ID might have not been assigned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unfollowUsers(Connection conn, String toBeDisconnecteUser) {
        //similar to follow, first check if username exists. if not, error handle.
        String query = "SELECT COUNT(*) FROM users WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            int idtostring = Integer.parseInt(toBeDisconnecteUser);
            stmt.setInt(1, idtostring);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next() && rs.getInt(1) == 0){
                    System.out.println("User with ID " + toBeDisconnecteUser + " does not exist.");
                    return;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return;
        }

        System.out.println("Enter your userID:");
        Scanner scanner = new Scanner(System.in);
        String thisuser = scanner.nextLine();


        String followQuery = "DELETE FROM follows WHERE follower = ? AND followee = ?";

        try (PreparedStatement stmt = conn.prepareStatement(followQuery)) {
            stmt.setInt(1, Integer.parseInt(thisuser));
            stmt.setInt(2, Integer.parseInt(toBeDisconnecteUser));
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(thisuser + " has unfollowed " + toBeDisconnecteUser);
            } else {
                System.out.println("Follow operation failed. The ID might have not been assigned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

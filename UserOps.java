import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class UserOps {

    // authenticate user with the database

    public static void followUsers(Connection conn, String toBeFollowedUser, int cUID){

        ///first, parse list of usernames and IDs and see if username exists
        //if not, error handling needed
        int followeeID = 0;
        String query = "SELECT e.email, e.uid, u.uid FROM emails AS e LEFT JOIN users AS u ON u.uid = e.uid WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, toBeFollowedUser);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next() == false){
                    System.out.println("User with Email " + toBeFollowedUser + " does not exist.");
                    return;
                }
                else{
                    followeeID = rs.getInt("uid");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return;
        }
        String username = "";
        query = "SELECT username FROM users WHERE uid = ? ";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() == false) {
                    System.out.println("User with ID " + cUID + " does not exist.");
                }
                else{
                    username = rs.getString("username");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }


        String followQuery = "INSERT INTO follows (Follower, Followee) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(followQuery)) {
            stmt.setInt(1, (cUID));
            stmt.setInt(2, followeeID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(username + " is now following " + toBeFollowedUser);
            } else {
                System.out.println("Follow operation failed. The ID might have not been assigned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unfollowUsers(Connection conn, String toBeDisconnecteUser, int cUID) {
        //similar to follow, first check if username exists. if not, error handle.
        int followeeID = 0;
        String query = "SELECT e.email, e.uid, u.uid FROM emails AS e LEFT JOIN users AS u ON u.uid = e.uid WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, toBeDisconnecteUser);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next() == false){
                    System.out.println("User with Email " + toBeDisconnecteUser + " does not exist.");
                    return;
                }
                else{
                    followeeID = rs.getInt("uid");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return;
        }

        String username = "";
        query = "SELECT username FROM users WHERE uid = ? ";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() == false) {
                    System.out.println("User with ID " + cUID + " does not exist.");
                }
                else{
                    username = rs.getString("username");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }



        String followQuery = "DELETE FROM follows WHERE follower = ? AND followee = ?";

        try (PreparedStatement stmt = conn.prepareStatement(followQuery)) {
            stmt.setInt(1, cUID);
            stmt.setInt(2, followeeID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(username + " has unfollowed " + toBeDisconnecteUser);
            } else {
                System.out.println("Follow operation failed. The ID might have not been assigned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

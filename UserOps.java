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


        String followQuery = "DELETE FROM follows WHERE followerID = ? AND followeeID = ?";

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

// import javax.xml.stream.events.Comment;
// import java.sql.*;
// import java.util.Scanner;
// import java.util.ArrayList;

// public class UserOps {


//     public static void login(Connection conn) {
//         Scanner scanner = new Scanner(System.in);
//         System.out.println("welcome to MovieMatrix, let's help you log in");

//         while (true){
//             System.out.println("\nEnter command in the format: Login <username> <password>");
//             String command = scanner.nextLine();
//             String[] userIn = command.split(" ");

//             if (userIn[0].equalsIgnoreCase("Login")){
//                 String username = userIn[1];
//                 String password = userIn[2];

//                 login(conn, username, password);
//             }else{
//                 System.out.println("Unknown command or incorrect usage. Please use 'Login <username> <password>'");
//             }
//         }
//     }

//     // authenticate user with the database
//     static void login(Connection conn, String username, String password){
//         String query = "SELECT password FROM users WHERE username = ?";

//         try(PreparedStatement stmt = conn.prepareStatement(query)){
//             stmt.setString(1, username);
//             ResultSet resultset = stmt.executeQuery();

//             if (resultset.next()){
//                 String storedPassword = resultset.getString("password");
//                 if (storedPassword.equals(password)){
//                     System.out.println("Login successful for user: " + username);
//                 }else{
//                     System.out.println("Login failed. incorrect password");
//                 }
//             }else{
//                 System.out.println("Login failed: username does not exist");
//             }
//         } catch (SQLException e){
//             System.out.println(e);
//         }
//     }

//     public static void followUsers(String username,Connection conn){
//         String searchSQL = "SELECT UID FROM users WHERE username = ?";
//         String insertSQL = "INSERT INTO follows (follower_id, followee) VALUES (?, ?)";


//         try(PreparedStatement stmt = conn.prepareStatement(insertSQL)){
//             stmt.setString(1, username);
//             ResultSet resultset = stmt.executeQuery();

//             if (resultset.next()){
//                 ///THIS HAS TO BE CHANGED
//                 String storedUID = resultset.getString("password");
//                 if (storedUID.equals("Blank")){
//                     ///THIS HAS TO BE CHANGED
//                     System.out.println("Follow successful for user: " + username);
//                 }else{
//                     System.out.println("Follow failed. Incorrect username or UID");
//                 }
//             }else{
//                 System.out.println("Follow failed: username does not exist");
//             }
//         } catch (SQLException e){
//             System.out.println(e);
//         }
//         ///first, parse list of usernames and IDs and see if username exists
//         //if not, error handling needed

//     }

//     public static void unfollowUsers(String username, Connection conn) {
//         //similar to follow, first check if username exists. if not, error handle.
//         String searchSQL = "SELECT UID FROM users WHERE username = username";
//         String insertSQL = "DELETE FROM follows (follower_id, followee) VALUES (?, ?)";

//         try(PreparedStatement stmt = conn.prepareStatement(insertSQL)){
//             stmt.setString(1, username);
//             ResultSet resultset = stmt.executeQuery();

//             if (resultset.next()){
//                 ///THIS HAS TO BE CHANGED
//                 String storedUID = resultset.getString("password");
//                 if (storedUID.equals("Blank")){
//                     ///THIS HAS TO BE CHANGED
//                     System.out.println("Follow successful for user: " + username);
//                 }else{
//                     System.out.println("Follow failed. Incorrect username or UID");
//                 }
//             }else{
//                 System.out.println("Follow failed: username does not exist");
//             }
//         } catch (SQLException e){
//             System.out.println(e);
//         }

//     }
// }
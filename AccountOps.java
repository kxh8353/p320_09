import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;



public class AccountOps {

    static int handlelogin(Connection conn){
        Scanner scanner = new Scanner(System.in);
        System.out.println("welcome to MovieMatrix, let's help you log in");

        while (true){
            System.out.println("\nEnter command in the format: Login <username> <password>");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");

            if (userIn.length == 3 && userIn[0].equalsIgnoreCase("Login")){
                String username = userIn[1];
                String password = userIn[2];

                int result = login(conn, username, password);
                if (result != -1){
                    return result;
                } else {
                    System.out.println("Login failed. please try again");
                }
            }else {
                System.out.println("Unknown command or incorrect usage. Please use 'Login <username> <password>'");
                return -1;
            }
        }
    }

    static int login(Connection conn, String username, String password){
        String query = "SELECT password, uid FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()){
                String storedPassword = resultset.getString("password");
                int uid = resultset.getInt("uid");
                if (storedPassword.equals(password)){
                    System.out.println("Login successful for user: " + username);
                    return uid;
                }else{
                    System.out.println("Login failed. incorrect password");
                }
            }else{
                System.out.println("Login failed: username does not exist");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    static void createAccount(Connection conn, String username, String password, String firstname, String lastname) {
        username = username.trim();
        int usernameCount = 0;

        int minimumPermittedID = 1500;
        int incrementID = minimumPermittedID;
        int newId = 0;

        String checkUsernameQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        String checkIdQuery = "SELECT COUNT(*) FROM users WHERE uid = ?"; // Assuming 'uid' is your ID column

        try (PreparedStatement checkUsernameStatement = conn.prepareStatement(checkUsernameQuery);
             PreparedStatement checkIdStatement = conn.prepareStatement(checkIdQuery)) {

            // Check username uniqueness
            checkUsernameStatement.setString(1, username);
            try (ResultSet rs = checkUsernameStatement.executeQuery()) {
                if (rs.next()) {
                    usernameCount = rs.getInt(1);
                    System.out.println("usernamecount: " + usernameCount);
                    if (usernameCount > 0) {
                        System.out.println("Username already exists.");
                        return; 
                    }
                }
            }

            // check for unique ID
            while (true) {
                checkIdStatement.setInt(1, incrementID);
                try (ResultSet rs = checkIdStatement.executeQuery()) {
                    if (rs.next()) {
                        int idCount = rs.getInt(1);
                        if (idCount == 0) {
                            newId = incrementID; // unique ID found
                            break;
                        }
                    }
                }
                incrementID++; // increment to check the next ID
            }

            System.out.println("New ID generated: " + newId);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        LocalDateTime currentTime = LocalDateTime.now();

        String insertQuery = "INSERT INTO users (username, password, firstname, lastname, date_made, uid) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);
            insertStatement.setString(3, firstname);
            insertStatement.setString(4, lastname);
            insertStatement.setTimestamp(5, Timestamp.valueOf(currentTime));
            insertStatement.setInt(6, newId);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account created successfully for user: " + username);
            } else {
                System.out.println("Account creation failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUserEmail(int uid, Connection conn){
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter email you would like to add to account");
        String email = scanner.nextLine();

        
        String query = "SELECT * FROM emails WHERE email = ? AND uid = ?";

        try (PreparedStatement viewStatement = conn.prepareStatement(query)){
            viewStatement.setString(1, email);
            viewStatement.setInt(2, uid);

            ResultSet rset = viewStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                rowsAffected++;
            }

            if (rowsAffected != 0) {
                System.out.println("This email is already associated with this user.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query2 = "INSERT INTO emails (email, uid) VALUES (?, ?)";
        try (PreparedStatement insertStatement = conn.prepareStatement(query2)){
            insertStatement.setString(1, email);
            insertStatement.setInt(2, uid);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Email not inserted.");
                return;
            }
            else{
                System.out.println("Email successfully inserted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}

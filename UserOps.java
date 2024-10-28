import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.ArrayList;

public class UserOps {


    private static final String DB_URL = "jdbc:mysql://localhost:127.0.0.1/p320_09"; 
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (authenticateUser(username, password)) {
            System.out.println("Login Successful!");

        } else {
            System.out.println("Invalid credentials, please try again.");
        }

        scanner.close();
    }

    // authenticate user with the database
    private static boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = DB_USERNAME AND password = DB_PASSWORD";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username); // compare
            statement.setString(2, password); // compare

            ResultSet rs = statement.executeQuery();
            return rs.next(); // success

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String username;
    private ArrayList<String> following = new ArrayList<>(); // List of usernames this user follows

    public UserOps(String username) {
        this.username = username;
        this.following = new ArrayList<>();
    }

    public void follow(String username) {
        this.following.add(username);
    }

    public ArrayList<String> getFollowing() {
        return following;
    }
    
}

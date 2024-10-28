import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class AccountOps {

    private static final String DB_URL = "jdbc:mysql://localhost:127.0.0.1/p320_09";
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to MovieMatrix! Lets help you create your new account!");

        System.out.println("Enter new username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        if (createAccount(username, password)) { // if not taken
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Account creation failed. Username might already exist.");
        }

        scanner.close();
    }

    static boolean createAccount(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (DB_USERNAME, DB_PASSWORD)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // return true if the account was created

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

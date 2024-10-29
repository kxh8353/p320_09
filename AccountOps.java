import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class AccountOps {

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/p320_09";
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to MovieMatrix! Let's help you create your new account!");

        while (true) {
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");

            if (userIn[0].equals("CreateAccount")) {
                createAccount(scanner); 
            } else {
                System.out.println("Unknown command. Please use 'CreateAccount <username> <password>'");
            }
        }
    }

    static void createAccount(Scanner scanner) {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();

        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (USERNAME, PASSWORD)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account creation failed. Username might already exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

    
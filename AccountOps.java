import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;



public class AccountOps {
//    private static Map<String, String> accounts = new HashMap<>();

    public static void AccountOpsMain(Connection conn, String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to MovieMatrix! Let's help you create your new account!");

        while (true) {
            System.out.print("\nEnter command in the format: CreateAccount <username> <password> <firstname> <lastname>");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");


//            try {

                if (userIn[0].equalsIgnoreCase("CreateAccount")) {
                    String username = userIn[1];
                    String password = userIn[2];
                    String firstname = userIn[3];
                    String lastname = userIn[4];

                    createAccount(conn, username, password, firstname, lastname);
                } else {
                    System.out.println("Unknown command or incorrect usage. Please use 'CreateAccount <username> <password> <firstname> <lastname>'");
                }
//            }catch (Exception e) {
//                System.out.println(e);
//            }
        }
    }

    static void createAccount(Connection conn, String username, String password, String firstname, String lastname) {

//        try {
//            Statement stmt = conn.createStatement();
//            String query = "INSERT INTO users (username, password) VALUES (username, password)";
//            ResultSet rset = stmt.executeQuery(query);
//
//
//            System.out.println("Account created successfully for user: " + username);
//        }catch(SQLException e){
//            e.printStackTrace();
//        }


        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement checkStatement = conn.prepareStatement(checkQuery)) {
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Account creation failed. Username already exists.");
                return; // Exit the method if the username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return; // Exit on error
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Random random = new Random();
        int randomIntInRange = random.nextInt(2000);


        String insertQuery = "INSERT INTO users (username, password, firstname, lastname, date_made, uid) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);
            insertStatement.setString(3, firstname);
            insertStatement.setString(4, lastname);
            insertStatement.setTimestamp(5, currentTimestamp);
            insertStatement.setInt(6, randomIntInRange);

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

}







// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.util.Scanner;

// public class AccountOps {

//     private static final String DatabaseURL = "jdbc:postgresql://127.0.0.1:5432/p320_09";
//     private static final String DatabaseUSERNAME = System.getenv("DB_USERNAME");
//     private static final String DatabasePASSWORD = System.getenv("DB_PASSWORD");

//     public static void AccountOpsMain(String[] args) {
//         Scanner scanner = new Scanner(System.in);
//         System.out.print("Welcome to MovieMatrix! Let's help you create your new account!");

//         while (true) {
//             System.out.print("\nEnter command: ");
//             String command = scanner.nextLine();
//             String[] userIn = command.split(" ");

//             if (userIn[0].equals("CreateAccount")) {
//                 createAccount(); 
//             } else {
//                 System.out.println("Unknown command. Please use 'CreateAccount <username> <password>'");
//             }
//         }
//     }

//     static void createAccount() {
//         Scanner scanner = new Scanner(System.in);
//         System.out.print("Enter new username: ");
//         String username = scanner.nextLine();

//         System.out.print("Enter new password: ");
//         String password = scanner.nextLine();

        
//         try{

//             Class.forName("org.postgresql.Driver");

//             try (Connection conn = DriverManager.getConnection(DatabaseURL, DatabaseUSERNAME, DatabasePASSWORD)) {
//                 String query = "INSERT INTO users (username, password) VALUES (USERNAME, PASSWORD)";
//                 PreparedStatement statement = conn.prepareStatement(query);
//                 statement.setString(1, username);
//                 statement.setString(2, password);
//                 int rowsAffected = statement.executeUpdate();

//                 if (rowsAffected > 0) {
//                     System.out.println("Account created successfully!");
//                 } else {
//                     System.out.println("Account creation failed. Username might already exist.");
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         } catch (Exception f){
//             f.printStackTrace();
//         }
//     }

// }

    
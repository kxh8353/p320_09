import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AccountOps {
    private static Map<String, String> accounts = new HashMap<>();

    public static void AccountOpsMain(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to MovieMatrix! Let's help you create your new account!");

        while (true) {
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");

            if (userIn[0].equalsIgnoreCase("CreateAccount") && userIn.length == 3) {
                String username = userIn[1];
                String password = userIn[2];
                createAccount(username, password);
            } else {
                System.out.println("Unknown command or incorrect usage. Please use 'CreateAccount <username> <password>'");
            }
        }
    }

    static void createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return; 
        }


        accounts.put(username, password);
        System.out.println("Account created successfully for user: " + username);
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

    
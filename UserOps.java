import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class UserOps {


    public static void UserOpsMain(Connection conn, String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("welcome to MovieMatrix, let's help you log in");

        while (true){
            System.out.println("\nEnter command in the format: Login <username> <password>");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");

            if (userIn[0].equalsIgnoreCase("Login")){
                String username = userIn[1];
                String password = userIn[2];

                login(conn, username, password);
            }else{
                System.out.println("Unknown command or incorrect usage. Please use 'Login <username> <password>'");
            }
        }
    }

    // authenticate user with the database
    static void login(Connection conn, String username, String password){
        String query = "SELECT password FROM users WHERE username = ?";

        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()){
                String storedPassword = resultset.getString("password");
                if (storedPassword.equals(password)){
                    System.out.println("Login successful for user: " + username);
                }else{
                    System.out.println("Login failed. incorrect password");
                }
            }else{
                System.out.println("Login failed: username does not exist");
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }

    private static void followUsers(String username, String password){

    }
    
}
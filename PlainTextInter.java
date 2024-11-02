import java.util.Scanner;
import com.jcraft.jsch.*;

// import movies.PostgresSSH.lib.jsch-0.1.55.src.main.java.com.jcraft.jsch.JSch;
// import movies.PostgresSSH.lib.jsch-0.1.55.src.main.java.com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
public class PlainTextInter {

    public static void main(String[] args) throws SQLException {
        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;

        String usernameFile = "";
        String passwordFile = "";

        try (BufferedReader br = new BufferedReader(new FileReader("CSAccountInfo.txt"))) {
            String line = br.readLine();

            if (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    usernameFile = parts[0];
                    passwordFile = parts[1];
                } else {
                    System.out.println("The file format is incorrect.");
                }
            }

            String user = usernameFile; // Change to your username
            String password = passwordFile; // Change to your password
            String databaseName = "p320_09"; // Change to your database name

            String driverName = "org.postgresql.Driver";
            Connection conn = null;
            Session session = null;

            try {
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                session = jsch.getSession(user, rhost, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                session.connect();
                System.out.println("Connected");
                int assigned_port = session.setPortForwardingL(lport, "127.0.0.1", rport);
                System.out.println("Port Forwarded");

                String url = "jdbc:postgresql://127.0.0.1:" + assigned_port + "/" + databaseName;

                System.out.println("Database Url: " + url);
                Properties props = new Properties();
                props.put("user", user);
                props.put("password", password);

                Class.forName(driverName);
                conn = DriverManager.getConnection(url, props);
                System.out.println("Database connection established");

                Scanner input = new Scanner(System.in);
                String[] userIn;
                boolean logined = false;
                int uidLoggedIn = -1;
                boolean quit = false;

                while (!quit) {
                    if (!logined) {
                        System.out.println("Enter command (e.g., Login <username> <password> or Help):");
                    } else {
                        System.out.println("Enter command (e.g., CreateCollection, SeeCollection, Follow, Unfollow, Logout, Help, etc...):");
                    }

                    String command = input.nextLine();
                    userIn = command.split(" ");

                    if (userIn[0].equalsIgnoreCase("Help")) {
                        System.out.println("Available commands:");

                        //Account ops
                        System.out.println(" 'Login' ");
                        System.out.println(" 'CreateAccount' ");

                        //Collection ops
                        System.out.println(" 'CreateCollection' ");
                        System.out.println(" 'ModifyCollectionName' ");
                        System.out.println(" 'SeeCollections' ");
                        System.out.println(" 'AddMovieToCollection' ");
                        System.out.println(" 'RemoveMovieFromCollection' ");
                        System.out.println(" 'DeleteCollection' ");

                        //Movie ops
                        System.out.println(" 'Search' ");
                        System.out.println(" 'Rate' 1-5 ");
                        System.out.println(" 'Watch' ");

                        //User ops
                        System.out.println(" 'Follow' ");
                        System.out.println(" 'Unfollow' ");

                        //Exit app / log out
                        System.out.println(" 'Logout' ");
                        System.out.println(" 'Exit' ");


                    // Login
                    } else if (userIn[0].equalsIgnoreCase("Login") && !logined) {
                        int result = AccountOps.handlelogin(conn);
                        if (result !=-1) {
                            logined = true; // login successful
                            uidLoggedIn = result;
                            System.out.println("Logged in");
                        } else {
                            System.out.println("Login failed. Please try again.");
                        }


                    // Log out
                    } else if (userIn[0].equalsIgnoreCase("Logout") && logined) {
                        logined = false; 
                        System.out.println("Logged out successfully.");

                    
                    // Create an account
                    } else if (userIn[0].equalsIgnoreCase("CreateAccount") && !logined) {
                        System.out.print("\nEnter in the format: CreateAccount <username> <password> <firstname> <lastname>");
                        command = input.nextLine();
                        userIn = command.split(" ");
                        if (userIn.length == 5) { 
                            AccountOps.createAccount(conn, userIn[1], userIn[2], userIn[3], userIn[4]);
                        } else {
                            System.out.println("Invalid format. Please try again.");
                        }


                    // Logged in actions
                    } else if (logined) {
                        switch (userIn[0].toLowerCase()) {

                            //Collection ops
                            case "createcollection":
                                System.out.println("Name your collection: ");
                                command = input.nextLine();
                                CollectionOps.CreateCollection(command, uidLoggedIn, conn);
                                break;
                            case "seecollection":
                                CollectionOps.SeeCollectionAll(uidLoggedIn, conn);
                                break;
                            case "addmovietocollection":
                                CollectionOps.AddMovieToCollection(uidLoggedIn, conn);
                                break;
                            case "deletemoviefromcollection":
                                CollectionOps.DeleteMovieFromCollection(uidLoggedIn, conn);
                                break;
                            case "deletemoviecollection":
                                CollectionOps.DeleteMovieCollection(uidLoggedIn, conn);
                                break;
                            case "modifycollectionname":
                                CollectionOps.ModifyCollectionName(uidLoggedIn, conn);
                                break;    

                            //Movie Ops
                            case "search":
                                MovieOps.search(conn);
                                break;
                            case "rate":
                                System.out.println("Enter movie you would like to rate");
                                String movieName = input.nextLine();

                                System.out.println("Enter its rating");
                                int rating = Integer.parseInt(input.nextLine());

                                MovieOps.rate(rating, movieName, uidLoggedIn, conn);

                                break;
                            case "watch":
                                System.out.println("Would you like to watch a movie or a collection?");
                                String choice = input.nextLine();
                                if(choice.toLowerCase().equals("movie")){
                                    System.out.println("Enter name of movie you would like to watch");
                                    movieName = input.nextLine();
                                    MovieOps.watch(movieName, conn, uidLoggedIn);

                                }
                                else if(choice.toLowerCase().equals("collection")){
                                    MovieOps.watchCollection(conn, uidLoggedIn);
                                }
                                else{
                                    System.out.println("Choice not recognized");
                                }
                                
                                break;
                            
                            // User Ops
                            case "follow":
                                System.out.println("Enter ID to follow:");
                                String userIDtoFollow = input.nextLine();
                                UserOps.followUsers(conn, userIDtoFollow);
                                break;
                            case "unfollow":
                                System.out.println("Enter Username or ID to Unfollow:");
//                                command = input.nextLine();
                                String userIDtoUnfollow = input.nextLine();
                                UserOps.unfollowUsers(conn, userIDtoUnfollow);
                                break;
                            case "exit":
                                quit = true; 
                                break;
                            default:
                                System.out.println("Invalid command; Try again");
                                break;
                        }
                    } else {
                        System.out.println("You must be logged in to perform this action.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("Closing Database Connection");
                    conn.close();
                }
                if (session != null && session.isConnected()) {
                    System.out.println("Closing SSH Connection");
                    session.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.util.ArrayList;

// import java.util.Scanner;
// import com.jcraft.jsch.*;


// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.util.Properties;
// import java.io.BufferedReader;
// import java.io.FileReader;
 
// public class PlainTextInter {


//     public static void main(String[] args) throws SQLException {
//         int lport = 5432;
//         String rhost = "starbug.cs.rit.edu";
//         int rport = 5432;

//         String usernameFile = "";
//         String passwordFile = "";

//         try (BufferedReader br = new BufferedReader(new FileReader("CSAccountInfo.txt"))) {
//             String line = br.readLine();

//             if (line != null) {
//                 String[] parts = line.split(" ");
//                 if (parts.length == 2) {
//                     usernameFile = parts[0];
//                     passwordFile = parts[1];
//                 } else {
//                     System.out.println("The file format is incorrect.");
//                 }
//             }
//             String user = usernameFile; //change to your username
//             String password = passwordFile; //change to your password
//             String databaseName = "p320_09"; //change to your database name

//             String driverName = "org.postgresql.Driver";
//             Connection conn = null;
//             Session session = null;
//             try {
//                 java.util.Properties config = new java.util.Properties();
//                 config.put("StrictHostKeyChecking", "no");
//                 JSch jsch = new JSch();
//                 session = jsch.getSession(user, rhost, 22);
//                 session.setPassword(password);
//                 session.setConfig(config);
//                 session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
//                 session.connect();
//                 System.out.println("Connected");
//                 int assigned_port = session.setPortForwardingL(lport, "127.0.0.1", rport);
//                 System.out.println("Port Forwarded");

//                 // Assigned port could be different from 5432 but rarely happens
//                 String url = "jdbc:postgresql://127.0.0.1:" + assigned_port + "/" + databaseName;

//                 System.out.println("database Url: " + url);
//                 Properties props = new Properties();
//                 props.put("user", user);
//                 props.put("password", password);

//                 Class.forName(driverName);
//                 conn = DriverManager.getConnection(url, props);
//                 System.out.println("Database connection established");
//                 Scanner input = new Scanner(System.in);
//                 String[] userIn;
//                 System.out.println("Welcome, Please Enter Your Command. Enter 'Help' to see commands");
//                 String command = input.nextLine();
//                 userIn = command.split(" ");
//                 boolean logined = false;
//                 boolean quit = false;
//                 while (!quit) {
//                     if (userIn[0].equals("Help")) {
//                         System.out.println(" 'Login' ");
//                         System.out.println(" 'CreateAccount' ");
//                         System.out.println(" 'CreateCollection' ");
//                         System.out.println(" 'SeeCollections' ");
//                         System.out.println(" 'Search' ");
//                         System.out.println(" 'ChangeCollection' ");
//                         System.out.println(" 'Rate' 1-5 ");
//                         System.out.println(" 'Watch' ");
//                         System.out.println(" 'Follow' ");
//                         System.out.println(" 'Unfollow' ");
//                         System.out.println(" 'Exit' ");
//                         command = input.nextLine();
//                         userIn = command.split(" ");
//                     }else if (userIn[0].equals("Login")) {
//                         if(AccountOps.login(conn) == true){
//                             logined = true;
//                             System.out.println("Logged in");
//                         }
//                         else{
//                             logined = false;
//                             System.out.println("User does not exist");
//                         }
//                     } else if (userIn[0].equals("CreateAccount")) {

//                         System.out.print("\nEnter in the format: CreateAccount <username> <password> <firstname> <lastname>");
//                         command = input.nextLine();
//                         userIn = command.split(" ");
//                         String username = userIn[0];
//                         String npassword = userIn[1];
//                         String firstname = userIn[2];
//                         String lastname = userIn[3];
//                         AccountOps.createAccount(conn, username, npassword, firstname, lastname);
//                         logined = true;
//                         command = input.nextLine();

//                     } else if (userIn[0].equals("CreateCollection")) {
//                         //TODO
//                         System.out.println("Name your collection: ");
//                         command = input.nextLine();

//                         ///UserOps.UserOpsMain(conn);
//                         CollectionOps.CreateCollection(command,conn);

//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     // } else if (userIn[0].equals("Login")) {

//                     //     System.out.println("please type: Login <username> <password>");
//                     //     UserOps.login(conn);

//                     } else if (userIn[0].equals("SeeCollection")) {
//                         //TODO
//                         CollectionOps.SeeCollection(conn);
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("Search")) {
//                         //TODO
//                         System.out.println("Enter Username or ID to search for");
//                         command = input.nextLine();
//                         MovieOps.search(command);
//                         if(!MovieOps.search(command)){
//                             System.out.println("Username DNE. Try again.");
//                         }else{
//                             System.out.println("Username Exists.");
//                         }
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("ChangeCollection")) {
//                         //TODO
//                         CollectionOps.ChangeCollection(conn);
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("Rate")) {
//                         //TODO
//                         if(logined) {
//                             System.out.println("Enter Movie name or ID to rate");
//                             command = input.nextLine();
//                             if (MovieOps.search(command)) {
//                                 System.out.println("Enter Rating of movie:");
//                                 int rating = Integer.parseInt(input.nextLine());
//                                 if (rating < 1 || rating > 5) {
//                                     System.out.println("Rating is out of bounds. Try again.");
//                                 } else {
//                                     MovieOps.rate(rating, command, conn);
//                                 }
//                             }
//                         }else{
//                             System.out.println("User not logged in. Try again.");
//                         }
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("Watch")) {

//                         //TODO
//                         if(logined) {
//                             System.out.println("Enter Movie name or ID to watch.");
//                             command = input.nextLine();
//                             if (MovieOps.search(command)) {
//                                 MovieOps.watch(command);
//                             } else {
//                                 System.out.println("Movie does not exist. Try again.");
//                             }
//                         }else{
//                             System.out.println("User not logged in. Try again.");
//                         }
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("Follow")) {
//                         //TODO
//                         //Does not check if user exists, should be done in userops
//                         if(logined) {
//                             System.out.println("Enter Username or ID to follow.");
//                             String username = input.nextLine();
//                             UserOps.followUsers(username,conn);
//                         }else{
//                             System.out.println("User not logged in. Try again.");
//                         }
//                         command = input.nextLine();
//                         userIn = command.split(" ");
//                     } else if (userIn[0].equals("Unfollow")) {
//                         if(logined) {
//                             System.out.println("Enter Username or ID to Unfollow.");
//                             command = input.nextLine();
//                             UserOps.unfollowUsers(command,conn);
//                             //TODO
//                         }else{
//                             System.out.println("User not logged in. Try again.");
//                         }
//                         command = input.nextLine();
//                         userIn = command.split(" ");

//                     } else if (userIn[0].equals("Exit")) {
//                         quit = true;
//                     } else {
//                         System.out.println("Invalid command; Try again");
//                         command = input.nextLine();
//                         userIn = command.split(" ");
//                     }
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             } finally {
//                 if (conn != null && !conn.isClosed()) {
//                     System.out.println("Closing Database Connection");
//                     conn.close();
//                 }
//                 if (session != null && session.isConnected()) {
//                     System.out.println("Closing SSH Connection");
//                     session.disconnect();
//                 }
//             }
//         }//for the data
//         catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
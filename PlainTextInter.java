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
                        System.out.println("Enter command (e.g., Login, CreateAccount, or Help):");
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
                        System.out.println(" 'AddEmailToAccount'");

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
                        System.out.println(" 'WatchTopTwenty' ");

                        //User ops
                        System.out.println(" 'Follow' ");
                        System.out.println(" 'Unfollow' ");

                        //Anaylitics Ops
                        System.out.println(" CollectionCount ");

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
                        System.out.println("\nEnter in the format: <username> <password> <firstname> <lastname>");
                        command = input.nextLine();
                        userIn = command.split(" ");
                        if (userIn.length == 4) { 
                            AccountOps.createAccount(conn, userIn[0], userIn[1], userIn[2], userIn[3]);
                        } else {
                            System.out.println("Invalid format. Please try again.");
                        }


                    // Logged in actions
                    } else if(userIn[0].equalsIgnoreCase("exit")){
                        quit = true; 
                        break;
                    }
                    
                    else if (logined) {
                        switch (userIn[0].toLowerCase()) {

                            //Account Ops
                            case "addemailtoaccount":
                                AccountOps.addUserEmail(uidLoggedIn, conn);
                                break;


                            //Collection Ops
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
                            
                            // MovieRecommendation ops
                            case "watchtoptwenty":
                                MovieReccomendation.Top20in90(conn);
                                break;
                            case "toptwentyamongfollowers":
                                MovieReccomendation.Top20moviesAmongFollowers(conn, uidLoggedIn);
                                break;
                            
                            //Anaylitics Ops
                            case "collectioncount":
                                AnaylticsOps.CollectionCount(conn, uidLoggedIn);
                                break;

                            // User Ops
                            case "follow":
                                System.out.println("Enter Email to Follow");
                                String emailToFollow = input.nextLine();
                                UserOps.followUsers(conn, emailToFollow, uidLoggedIn);
                                break;
                            case "unfollow":
                                System.out.println("Enter Email to Unfollow");
                                String userIDtoUnfollow = input.nextLine();
                                UserOps.unfollowUsers(conn, userIDtoUnfollow, uidLoggedIn);
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

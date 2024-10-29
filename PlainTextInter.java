import java.util.ArrayList;
import java.util.Scanner;

import com.jcraft.jsch.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
 
public class PlainTextInter {


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] userIn;
        System.out.println("Welcome, Please Enter Your Command. Enter 'Help' to see commands");
        String command = input.nextLine();
        userIn = command.split(" ");
        boolean quit = false;
        while (!quit) {
            if(userIn[0].equals("Help")) {
                System.out.println(" 'Login' Username Password ");
                System.out.println(" 'CreateAccount' Username Password ");
                System.out.println(" 'CreateCollection' Name ");
                System.out.println(" 'SeeCollections' ");
                System.out.println(" 'Search' Movie ");
                System.out.println(" 'ChangeCollection' Name ");
                System.out.println(" 'Rate' 1-5 ");
                System.out.println(" 'Watch' Movie/Collection Name ");
                System.out.println(" 'Follow' Username");
                System.out.println(" 'Unfollow' Username");
                System.out.println(" 'Exit' ");
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("CreateAccount")) {
            
                // command = input.nextLine();
                // userIn = command.split(" ");

                AccountOps.createAccount(input);

            }
            else if(userIn[0].equals("CreateCollection")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Login")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("SeeCollection")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Search")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("ChangeCollection")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Rate")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Watch")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Follow")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Unfollow")) {
                //TODO
                command = input.nextLine();
                userIn = command.split(" ");
            }
            else if(userIn[0].equals("Exit")) {
                quit = true;
            }
            else {
                System.out.println("Invalid command; Try again");
                command = input.nextLine();
                userIn = command.split(" ");
            }
        }
    }


public static void connect(){ 
        
        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;
        String user = "YOUR_CS_USERNAME"; //change to your username
        String password = "YOUR_CS_PASSWORD"; //change to your password
        String databaseName = "YOUR_DB_NAME"; //change to your database name

        String driverName = "org.postgresql.Driver";
        Connection conn = null;
        Session session = null;
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(user, rhost, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");
            session.connect();
            System.out.println("Connected");
            int assigned_port = session.setPortForwardingL(lport, "127.0.0.1", rport);
            System.out.println("Port Forwarded");

            // Assigned port could be different from 5432 but rarely happens
            String url = "jdbc:postgresql://127.0.0.1:"+ assigned_port + "/" + databaseName;

            System.out.println("database Url: " + url);
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);

            Class.forName(driverName);
            conn = DriverManager.getConnection(url, props);
            System.out.println("Database connection established");

            // Do something with the database....

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
        }}//for the data




}
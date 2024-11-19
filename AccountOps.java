import javax.xml.transform.Result;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

import java.util.Base64;

public class AccountOps {
    static Cipher cipher;
    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Specify the transformation
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing cipher");
        }
    }
    static byte[] keyBytes = "1234567890123456".getBytes(); // Ensure it matches the key size requirements
    static SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

    public SecretKey returnEncrypt(){
        return secretKey;
    }

    static int handlelogin(Connection conn) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to MovieMatrix, let's help you log in");

        while (true){
            System.out.println("\nEnter user information in the format: <username> <password>");
            String command = scanner.nextLine();
            String[] userIn = command.split(" ");

            if (userIn.length == 2){
                String username = userIn[0];
                String password = userIn[1];



               /// int result = login(conn, username, encrypt(password,secretKey));
                int result = login(conn, username, password);
                if (result != -1){
                    return result;
                } else {
                    System.out.println("Login failed. please try again");
                }
            }else {
                System.out.println("Unknown command or incorrect usage. Please enter in format '<username> <password>'");
                return -1;
            }
        scanner.close();
        }
    }

    static int login(Connection conn, String username, String password){
        String query = "SELECT password, uid FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()){


                String storedPassword = resultset.getString("password");
                if(isEncrypted(storedPassword,secretKey)){
                    storedPassword = decrypt(storedPassword,secretKey);
                }

                int uid = resultset.getInt("uid");
                if (storedPassword.equals(password)){
                    System.out.println("Login successful for user: " + username);
                    updateTimeLoggedIn(conn, uid);
                    return uid;
                }else{
                    System.out.println("Login failed. incorrect password");
                }
            }else{
                System.out.println("Login failed: username does not exist");
            }
        } catch (SQLException e){
            e.printStackTrace();
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return -1;
    }

    static void updateTimeLoggedIn(Connection conn, int uid) {
        String updateQuery = "INSERT INTO login (login, uid ) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            LocalDateTime currentTime = LocalDateTime.now();

            stmt.setTimestamp(1, Timestamp.valueOf(currentTime));  
            stmt.setInt(2, uid);  

            stmt.executeUpdate();
            System.out.println("Login time recorded for user ID: " + uid + " at " + currentTime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean isEncrypted(String password, SecretKey key) {
        try {
            // Attempt decryption (assume decrypt is implemented)
            decrypt(password, key);
            return true; // If decryption succeeds, it's encrypted data
        } catch (Exception e) {
            return false; // Decryption failed, likely not encrypted
        }
    }
     static void createAccount(Connection conn, String username, String password, String firstname, String lastname) throws Exception {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }


        LocalDateTime currentTime = LocalDateTime.now();

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // block size is 128bits


            password = encrypt(password,secretKey);


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

        
        String query = "SELECT email FROM emails WHERE email = ? AND uid = ?";

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

    public static String encrypt(String password, SecretKey key) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] plainTextByte = password.getBytes();
        byte[] encryptedByte = cipher.doFinal(plainTextByte);

        // Combine IV and encrypted text
        byte[] combined = new byte[iv.length + encryptedByte.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedByte, 0, combined, iv.length, encryptedByte.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String password, SecretKey key) throws Exception{
        ///password = encrypt(password,key);
        byte[] combined = Base64.getDecoder().decode(password);

        // Extract IV (first 16 bytes)
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract the actual encrypted data (remaining bytes)
        byte[] encryptedTextByte = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedTextByte, 0, encryptedTextByte.length);

        // Initialize the cipher in DECRYPT_MODE with the extracted IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // Decrypt the data
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);

        // Return the decrypted data as a string
        return new String(decryptedByte);
    }
}

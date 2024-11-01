import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;
import com.jcraft.jsch.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;



public class CollectionOps {

    public static void CreateCollection(String name, int uid, Connection conn){
        int minimumPermittedID = 2500;
        int incrementID = minimumPermittedID;
        int newId = 0;

        String checkIdQuery = "SELECT COUNT(*) FROM collections WHERE collectionid = ?"; // Assuming 'uid' is your ID column

        try (PreparedStatement checkIdStatement = conn.prepareStatement(checkIdQuery)) {
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

        String insertQuery = "INSERT INTO collections (collectionid, name, uid) VALUES (?, ?, ?)";

        try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)){
            insertStatement.setInt(1, newId);
            insertStatement.setString(2, name);
            insertStatement.setInt(3, uid);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Collection Created Successfully!");
            } else {
                System.out.println("Collection creation failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }


    public static void SeeCollectionAll(int uid, Connection conn){
        String query = "SELECT * FROM collections WHERE uid = ? ORDER BY name";

        try (PreparedStatement viewStatement = conn.prepareStatement(query)){
            viewStatement.setInt(1, uid);

            ResultSet rset = viewStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                int collectionID = rset.getInt("collectionid");
                String collectionName = rset.getString("name");
                System.out.print((rowsAffected+1) + ": ");
                SeeCollection(collectionID, collectionName, conn);
                System.out.println();

                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("No collections under this user.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void SeeCollection(int collectionID, String name, Connection conn){
        // Get number of movies in collection
        int numMovies = 0;
        String query2 = "SELECT COUNT(*) AS moviecount FROM contains WHERE collectionid = ?";

        try (PreparedStatement viewStatement = conn.prepareStatement(query2)){
            viewStatement.setInt(1, collectionID);

            ResultSet rset = viewStatement.executeQuery();

            while(rset.next()) {   // Move the cursor to the next row
                numMovies = rset.getInt("moviecount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get total length in minutes
        int totalLengthMins = 0;
        String query3 = "SELECT movieid FROM contains WHERE collectionid = ?";

        try (PreparedStatement viewStatement = conn.prepareStatement(query3)){
            viewStatement.setInt(1, collectionID);

            ResultSet rset = viewStatement.executeQuery();

            while(rset.next()) {   // Move the cursor to the next row
                int movieId = rset.getInt("movieid");
                

                //Get total length in minutes
                String query4 = "SELECT lengthinminutes FROM movies WHERE movieid = ?";
                try (PreparedStatement movieStatement = conn.prepareStatement(query4)){
                    movieStatement.setInt(1, movieId);

                    ResultSet rset2 = movieStatement.executeQuery();
                    while(rset2.next()) { 
                        totalLengthMins+= rset2.getInt("lengthinminutes");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Collection Name: " + name);
        System.out.println("Total number of movies in collection: " + numMovies);
        if(totalLengthMins !=0){
            System.out.println("Length of movies in collection " + totalLengthMins/60 + "hrs " + totalLengthMins%60 + "mins");
        }
        else{
            System.out.println("Length of movies in collection 0 hrs 0 mins");
        }
        
    }


    // public static void ChangeCollection(int uid, Connection conn){
    //     String query = "UPDATE collections SET collectionID = ? AND name = ? AND uid = ?";
    // }


    // Needs to be mapped
    public static void AddMovieToCollection(int uid, Connection conn){
        //String query = "INSERT into collections (collectionID, name, uid) VALUES (?, ?, ?)";

        Scanner input = new Scanner(System.in);

        System.out.println("Enter name of collection you would like to add a movie to: ");
        String collectionName = input.nextLine();
        int collectionID = -1;
        int movieID = -1;


        //Get collection id
        String query = "SELECT * FROM collections WHERE uid = ? AND name = ?";

        try (PreparedStatement viewStatement = conn.prepareStatement(query)){
            viewStatement.setInt(1, uid);
            viewStatement.setString(1, collectionName);

            ResultSet rset = viewStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                collectionID = rset.getInt("collectionid");
                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("No collection with this name.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get movie id
        String query2 = "SELECT movieid FROM movies WHERE name = ?";
        System.out.println("Enter name of the movie you would like to add to the collection: ");
        String movieName = input.nextLine();

        try (PreparedStatement viewStatement = conn.prepareStatement(query2)){
            viewStatement.setString(1, movieName);

            ResultSet rset = viewStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                movieID = rset.getInt("movieid");
                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("No movie with this name.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Add movieid and collection id to contains
        String query3 = "INSERT INTO contains (movieid, collectionid) VALUES (?, ?)";

        try (PreparedStatement insertStatement = conn.prepareStatement(query3)){
            insertStatement.setInt(1, movieID);
            insertStatement.setInt(2, collectionID);

            ResultSet rset = insertStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                movieID = rset.getInt("movieid");
                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("Insertion Unsuccesful.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully inserted movie!");

    }


    // Needs to be mapped
    public static void DeleteMovieFromCollection(int uid, Connection conn){
        String query = "INSERT into collections (collectionID, name, uid) VALUES (?, ?, ?)";
    }


    //Needs to be mapped to PTUI
    public static void DeleteMovieCollection(int uid, Connection conn){
        Scanner input = new Scanner(System.in);

        System.out.println("Enter name of collection you would like to delete: ");
        String collectionName = input.nextLine();
        int collectionID = -1;

        String query = "DELETE FROM collections WHERE name = ? AND uid = ?";

        try (PreparedStatement deleteStatement = conn.prepareStatement(query)){
            deleteStatement.setString(1, collectionName);
            deleteStatement.setInt(2, uid);

            ResultSet rset = deleteStatement.executeQuery();
            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                collectionID = rset.getInt("collectionid");
                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("No collection found with this name.");
                return;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query2 = "DELETE FROM contains WHERE collectionid = ?";

        try (PreparedStatement deleteStatement = conn.prepareStatement(query2)){
            deleteStatement.setInt(1, collectionID);

            ResultSet rset = deleteStatement.executeQuery();
            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                rowsAffected++;
            }

            if (rowsAffected == 0) {
                System.out.println("No references to this collection found within contains.");
                return;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully deleted collection!");
    }


    //Needs to be mapped
    public static void ModifyCollectionName(int uid, Connection conn){

        Scanner input = new Scanner(System.in);

        System.out.println("Enter name of collection you would like to modify: ");
        String collectionNameOld = input.nextLine();

        System.out.println("Enter new name for the collection: ");
        String collectionNameNew = input.nextLine();


        String query1 = "SELECT * FROM collections WHERE name = ? AND uid = ?";
        try (PreparedStatement viewStatement = conn.prepareStatement(query1)){
            viewStatement.setString(1, collectionNameNew);
            viewStatement.setInt(2, uid);

            ResultSet rset = viewStatement.executeQuery();

            int rowsAffected = 0;

            while(rset.next()) {   // Move the cursor to the next row
                rowsAffected++;
            }
            if(rowsAffected > 0 ){
                System.out.println("This name already exists in your collection library");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        String query = "UPDATE collections SET name = ? WHERE name = ?";

        try (PreparedStatement updateStatement = conn.prepareStatement(query)){
            updateStatement.setString(1, collectionNameNew);
            updateStatement.setString(2, collectionNameOld);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No collection found with this name to update.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully modified collection name!");

    }


}



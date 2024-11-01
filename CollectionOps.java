import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;
import com.jcraft.jsch.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;



public class CollectionOps {

    public static void CreateCollection(String name, Connection conn){

        Statement stmt;
        try {
            stmt = conn.createStatement();
            String strSelect = "INSERT into collections (collectionID, name, uid) VALUES (?, ?, ?)";
            // ResultSet rset = stmt.executeQuery(strSelect);

            // while(rset.next()) {   // Move the cursor to the next row
            //     String title = rset.getString("title");
            //     System.out.println(title);
            // }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void SeeCollection(Connection conn){
        String query = "SELECT * FROM collections WHERE collectionID = ? AND name = ? AND uid = ?";
    }

    public static void ChangeCollection(Connection conn){
        String query = "UPDATE collections SET collectionID = ? AND name = ? AND uid = ?";
    }

    public static void AddMovieCollection(Connection conn){
        String query = "INSERT into collections (collectionID, name, uid) VALUES (?, ?, ?)";
    }

    public static void DeleteMovieCollection(Connection conn){
        String query = "DELETE FROM collections WHERE collectionID = ? AND name = ? AND uid = ?";
    }

    public static void ModifyCollectionName(Connection conn){
        String query = "UPDATE collections SET name = ?";
    }

}



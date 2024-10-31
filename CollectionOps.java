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

    public static void CreateCollection(Connection conn){

        Statement stmt;
        try {
            stmt = conn.createStatement();
            String strSelect = "insert query here";
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

    }

    public static void ChangeCollection(Connection conn){

    }

    public static void AddMovieCollection(Connection conn){

    }

    public static void DeleteMovieCollection(Connection conn){

    }

    public static void ModifyCollectionName(Connection conn){

    }

}



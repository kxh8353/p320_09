import java.sql.Connection;import java.io.FileNotFoundException;
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


public class MovieOps {

    public static void rate(int rating, String movie, Connection conn) {

        //Rating has been checked for correctness, movie exists and user is logged in.

        String search = "SELECT uid FROM movies WHERE movie = ?";

        try (PreparedStatement stmt = conn.prepareStatement(search)) {
            stmt.setString(1, movie);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {
                int uid = resultset.getInt("uid");

                // Assuming you want to insert a rating for this movie if it exists
                String insertRating = "INSERT INTO ratings (uid, rating) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertRating)) {
                    insertStmt.setInt(1, uid);
                    insertStmt.setInt(2, rating);
                    insertStmt.executeUpdate();
                    System.out.println("Rating added successfully.");
                }

            } else {
                System.out.println("movie does not exist.");//?SHOULD NOT HAPPEN

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean search(String username, Connection conn) {
        //ptui handles the output, this should just determine if username exists
        // and return true or false. String username can be a username or a userID
        String search = "SELECT username FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(search)) {
            stmt.setString(1, username);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()) {

                System.out.println("Username found");
                return true;
            } else {
                System.out.println("Username does not exist.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

}

    public static void watch(String movie,Connection conn, String userID) {

         String search = "SELECT MovieID FROM movies WHERE movie = ?";
        try (PreparedStatement stmt = conn.prepareStatement(search)){
            stmt.setString(1, movie);
            ResultSet resultset = stmt.executeQuery();

            if (resultset.next()){
                int movieID = resultset.getInt("MovieID");
                String insertWatched = "INSERT INTO watched_movies (user_id, movie_id) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertWatched)) {
                        insertStmt.setInt(1, Integer.parseInt(userID));
                        insertStmt.setInt(2, movieID);
                        insertStmt.executeUpdate();
                        System.out.println("Movie added to watched list.");
            }

                }else{
                    System.out.println("Movie does not exist.");

                }

        } catch (SQLException e){
            e.printStackTrace();
        }
        //movie exists, user is logged in
    }
}


import java.sql.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserProfileOps {

    public static void displayProfile(Connection conn, int currentUser){

        Scanner input = new Scanner(System.in);

        UserInfo(conn, currentUser);

        CollectionCount(conn, currentUser);
        System.out.println();
        FollowersCount(conn, currentUser);
        System.out.println();
        FollowingCount(conn, currentUser);

        System.out.println("\nWould you like to see your top 10 movies based on watch history, ratings, or both?" + 
        " Enter 'W' for watch history, 'R' for ratings, or 'WR' for both.");
        String command = input.nextLine();

        System.out.println();
        if(command.equalsIgnoreCase("r")){
            Top10MoviesRating(conn, currentUser);
        }
        else if(command.equalsIgnoreCase("w")){
            Top10MoviesWatches(conn, currentUser);
        }
        else if(command.equalsIgnoreCase("wr")){
            Top10MoviesWatchesRatings(conn, currentUser);
        }
        else{
            System.out.println("Command not recognized.");
        }

        System.out.println();

    }

    public static void UserInfo(Connection conn, int currentUser){
        String Query = "SELECT firstname FROM users WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String name = rs.getString("firstname");
                System.out.println("\n--Hello " + name + ", here is your movie matrix profile information.--\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void CollectionCount(Connection conn, int currentUser){
        String Query = "SELECT COUNT(name) as total FROM collections WHERE uid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("total");
                System.out.println("Number of Collections: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void FollowersCount(Connection conn, int currentUser){
        //KC - Check for correctness 
        String Query = "SELECT COUNT(f.followee) AS follower_count\n" + //
                        "FROM users AS u\n" + //
                        "LEFT JOIN follows AS f ON u.uid = f.followee\n" + //
                        "WHERE u.uid = ?\n" + //
                        "";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("follower_count");
                System.out.println("Number of Users Following you: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void FollowingCount(Connection conn, int currentUser){
        //KC - Check for correctness 
        String Query = "SELECT COUNT(f.follower) AS following_count\n" + //
                        "FROM users AS u\n" + //
                        "LEFT JOIN follows AS f ON u.uid = f.follower\n" + //
                        "WHERE u.uid = ?\n" + //
                        "";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int count = rs.getInt("following_count");
                System.out.println("Number of Users you are Following: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void Top10MoviesRating(Connection conn, int currentUser){
        //KC - Check for correctness 
        String Query = "SELECT m.title, m.mpaa, AVG(r.number_of_stars) AS rating FROM movies AS m\n" + //
                        "LEFT JOIN rates AS r ON r.movieid = m.movieid\n" + //
                        "WHERE r.uid = ?\n" + //
                        "GROUP BY m.movieid, m.title\n" + //
                        "HAVING COUNT(r.number_of_stars) > 0\n" + //
                        "ORDER BY rating DESC\n" + //
                        "LIMIT 10;";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while(rs.next()){
                if(count == 0){
                    System.out.println("Your Top 10 Movies Based on Rating!");
                }
                String title = rs.getString("title");
                String rating = rs.getString("mpaa");
                count++;
                System.out.println(count + ". " + title + ", " + rating.toUpperCase());
            }
            if(count ==0){
                System.out.println("User has not rated any movies");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void Top10MoviesWatches(Connection conn, int currentUser){
        //KC - Check for correctness 
        String Query = "SELECT m.title, m.mpaa, COUNT(w.movieid) AS watch_count FROM movies AS m\n" + //
                        "LEFT JOIN watched AS w ON m.movieid = w.movieid\n" + //
                        "WHERE w.uid = ?\n" + //
                        "GROUP BY m.title, m.mpaa, m.movieid\n" + //
                        "ORDER BY watch_count DESC\n" + //
                        "LIMIT 10";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while(rs.next()){
                if(count == 0){
                    System.out.println("Your Top 10 Movies Based on Watch History!");
                }
                String title = rs.getString("title");
                String rating = rs.getString("mpaa");
                count++;
                System.out.println(count + ". " + title + ", " + rating.toUpperCase());
            }
            if(count ==0){
                System.out.println("User has not watched any movies");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void Top10MoviesWatchesRatings(Connection conn, int currentUser){
        //KC - Check for correctness 
        String Query = "SELECT m.movieid, m.title, m.mpaa, COUNT(w.movieid) AS watch_count, AVG(r.number_of_stars) AS rating FROM movies AS m\n" + //
                        "LEFT JOIN watched AS w ON m.movieid = w.movieid\n" + //
                        "LEFT JOIN rates AS r on m.movieid = r.movieid\n" + //
                        "WHERE w.uid = ?\n" + //
                        "GROUP BY m.title, m.mpaa, m.movieid\n" + //
                        "HAVING COUNT(w.movieid) > 0 OR COUNT(r.number_of_stars) > 0\n" + //
                        "ORDER BY watch_count DESC, rating DESC\n" + //
                        "LIMIT 10";
        try (PreparedStatement stmt = conn.prepareStatement(Query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while(rs.next()){
                if(count == 0){
                    System.out.println("Your Top 10 Movies Based on Watch History and Ratings!");
                }
                String title = rs.getString("title");
                String rating = rs.getString("mpaa");
                count++;
                System.out.println(count + ". " + title + ", " + rating.toUpperCase());
            }
            if(count ==0){
                System.out.println("User has not watched or rated any movies");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    
}

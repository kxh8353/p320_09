import java.sql.Connection;

public class MovieOps {

    public static void rate(int rating, String movie, Connection conn){
        //Rating has been checked for correctness, movie exists and user is logged in.

    }
    public static boolean search(String username){
        //ptui handles the output, this should just determine if username exists
        // and return true or false. String username can be a username or a userID

        return false;
    }

    public static void watch(String movie) {
        //movie exists, user is logged in
    }
}

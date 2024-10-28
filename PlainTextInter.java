import java.util.ArrayList;
import java.util.Scanner;

 
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
}
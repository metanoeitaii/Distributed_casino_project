package player;

import java.net.Socket;//gia TCP syndesh me ton Master
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import common.Message;

public class Test {

    private static String MANAGER_IP;//pou einai o manager
    private static int MANAGER_PORT;//port tou manager

    public static void main(String[] args) {
        MANAGER_IP = args[0];//pou einai o manager
        MANAGER_PORT = Integer.parseInt(args[1]);//port tou manager
        
        Scanner scanner = new Scanner(System.in);

        System.out.println("====================================");
        System.out.println("      DUMMY PLAYER APP START");
        System.out.println("====================================");

        System.out.print("Give Player ID: ");
        String playerId = scanner.nextLine().trim();//xreiazetai gia ADD_BALANCE kai PLAY 

        while (true) {//tha trexei synexeia to menu mexri na dialejei Exit
            System.out.println();
            System.out.println("============== MENU ==============");
            System.out.println("1. Search games");
            System.out.println("2. Add balance");
            System.out.println("3. Play game");
            System.out.println("4. Vote game");
            System.out.println("5. Exit");
            System.out.print("Select (1-5): ");

            String choice = scanner.nextLine().trim();

            //to scaner pernaei stis methodous gia na mporoyn na diavasoun apo to terminal
            switch (choice) {
                case "1":
                    handleSearch(scanner);
                    break;
                case "2":
                    handleAddBalance(scanner, playerId);
                    break;
                case "3":
                    handlePlay(scanner, playerId);
                    break;
                case "4":
                    handleVote(scanner);
                    break;
                case "5":
                    System.out.println("End of Dummy Player App.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Not a valid choice. Please select 1-5.");
            }
        }
    }

    private static void handleSearch(Scanner scanner) {
        System.out.println();
        System.out.println("----- SEARCH GAMES -----");

        System.out.print("Give bet category (ALL / $ / $$ / $$$): ");
        String betCategory = scanner.nextLine().trim();

        System.out.print("Give risk level (ALL / low / medium / high): ");
        String riskLevel = scanner.nextLine().trim();

        System.out.print("Give minimum stars (e.g., 0, 3.5, 4): ");
        String minStars = scanner.nextLine().trim();

        //an o user pathsei apla enter xwris na dwsei timi, mpainoyn aytomata ta default
        if (betCategory.isEmpty()) {
            betCategory = "ALL";
        }
        if (riskLevel.isEmpty()) {
            riskLevel = "ALL";
        }
        if (minStars.isEmpty()) {
            minStars = "0";
        }

        try (
            Socket socket = new Socket(MANAGER_IP, MANAGER_PORT);//tcp syndesh me master
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            //auta stelnontai ston master gia na kanei search me ta sygkekrimena filters
            out.writeObject(Message.SEARCH);
            out.writeObject(betCategory);
            out.writeObject(riskLevel);
            out.writeObject(minStars);
            out.flush();

            List<GameSearchResult> results = new ArrayList<>();//lista pou tha krataei ta apotelesmata poy epistrefei o manager

            while (true) {//teleixnei otan o manager steilei END
                String firstField = (String) in.readObject();//diavazei to proto pedio pou stelnei o manager(END h game name)

                if (firstField.equals(Message.END)) {
                    break;
                }

                GameSearchResult game = new GameSearchResult();//krataei ta stoixia enos game
                game.gameName = firstField;
                game.providerName = (String) in.readObject();
                game.gameLogo = (String) in.readObject();
                game.stars = String.valueOf(in.readObject());
                game.noOfVotes = String.valueOf(in.readObject());
                game.minBet = String.valueOf(in.readObject());
                game.maxBet = String.valueOf(in.readObject());
                game.riskLevel = (String) in.readObject();
                game.betCategory = (String) in.readObject();
                game.jackpot = String.valueOf(in.readObject());

                results.add(game);
            }

            if (results.isEmpty()) {
                System.out.println("No games found matching the filters.");
                return;
            }

            System.out.println();
            System.out.println("----- SEARCH RESULTS -----");
            for (int i = 0; i < results.size(); i++) {
                GameSearchResult gamee = results.get(i);
                System.out.println("Game #" + (i + 1));
                System.out.println("Name       : " + gamee.gameName);
                System.out.println("Provider   : " + gamee.providerName);
                System.out.println("Logo       : " + gamee.gameLogo);
                System.out.println("Stars      : " + gamee.stars);
                System.out.println("Votes      : " + gamee.noOfVotes);
                System.out.println("Min Bet    : " + gamee.minBet);
                System.out.println("Max Bet    : " + gamee.maxBet);
                System.out.println("Risk Level : " + gamee.riskLevel);
                System.out.println("Category   : " + gamee.betCategory);
                System.out.println("Jackpot    : " + gamee.jackpot);
                System.out.println("------------------------------------");
            }

        } catch (Exception e) {
            System.out.println("Error in SEARCH: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleAddBalance(Scanner scanner, String playerId) {//gia na balei lefta o player 
        System.out.println();
        System.out.println("----- ADD BALANCE -----");

        System.out.print("Give amount to add: ");
        String amount = scanner.nextLine().trim();

        try (
            Socket socket = new Socket(MANAGER_IP, MANAGER_PORT);//tcp syndesh me master
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            //stelnoyme ston master ta aparaitita gia na kanei Add Balance
            out.writeObject(Message.ADD_BALANCE);
            out.writeObject(playerId);
            out.writeObject(amount);
            out.flush();

            String status = (String) in.readObject();//(OK h ERROR) pou epistrefei o master
            
            if (!status.equals(Message.OK)) {
                System.out.println("Something went wrong: " + status);
            }

        } catch (Exception e) {
            System.out.println("Error in ADD_BALANCE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handlePlay(Scanner scanner, String playerId) {
        System.out.println();
        System.out.println("----- PLAY GAME -----");

        System.out.print("Give game name: ");
        String gameName = scanner.nextLine().trim();

        System.out.print("Give bet amount: ");
        String betAmount = scanner.nextLine().trim();

        try (
            Socket socket = new Socket(MANAGER_IP, MANAGER_PORT);//tcp syndesh me master
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            //stelnoyme ston master ta aparaitita gia na kanei Play
            out.writeObject(Message.PLAY);
            out.writeObject(gameName);
            out.writeObject(playerId);
            out.writeObject(betAmount);
            out.flush();

            String status = (String) in.readObject();//(OK h ERROR) pou epistrefei o master 

            if (!status.equals(Message.OK)) {
                System.out.println("Something went wrong: " + status);
                return;
            }

            double result = (double) in.readObject();//to apotelesma tou paixnidiou (kerdos h zhmia)
            String type = (String) in.readObject();//(NORMAL h JACKPOT)

            System.out.println("Status: " + status);

            if (result > 0) {
                System.out.println("Profit: +" + result);
            } else if (result < 0) {
                System.out.println("Loss: " + result);
            } else {
                System.out.println("Result: " + result);
            }

            if (type.equals(Message.JACKPOT)) {
                System.out.println(">>> JACKPOT! <<<");
            } else {
                System.out.println("Normal result.");
            }

        } catch (Exception e) {
            System.out.println("Error sto PLAY: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleVote(Scanner scanner) {//gia rating game
        System.out.println();
        System.out.println("----- VOTE GAME -----");

        System.out.print("Give game name: ");
        String gameName = scanner.nextLine().trim();

        System.out.print("Give stars (1-5): ");
        String stars = scanner.nextLine().trim();

        try (
            Socket socket = new Socket(MANAGER_IP, MANAGER_PORT);//tcp syndesh me master
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            //stelnoyme ston master ta aparaitita gia na kanei Vote
            out.writeObject(Message.VOTE);
            out.writeObject(gameName);
            out.writeObject(stars);
            out.flush();

            String status = (String) in.readObject();//(OK h ERROR) pou epistrefei o master
            System.out.println("The status of your vote is: " + status);

        } catch (Exception e) {
            System.out.println("Error sto VOTE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class GameSearchResult {//gia na krataei ta stoixia enos game poy epistrefei o master otan kanei search
        String gameName;
        String providerName;
        String gameLogo;
        String stars;
        String noOfVotes;
        String minBet;
        String maxBet;
        String riskLevel;
        String betCategory;
        String jackpot;
    }
}

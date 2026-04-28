package player;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import common.Message;

public class Test {

    private static String ClientHANDLER_IP;
    private static int ClientHandler_PORT;

    public static void main(String[] args) {
        ClientHANDLER_IP = args[0];
        ClientHandler_PORT = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);

        System.out.println("====================================");
        System.out.println("      DUMMY PLAYER APP START");
        System.out.println("====================================");

        System.out.print("Give Player ID: ");
        String playerId = scanner.nextLine().trim();

        while (true) {
            System.out.println();
            System.out.println("============== MENU ==============");
            System.out.println("1. Search games");
            System.out.println("2. Add balance");
            System.out.println("3. Play game");
            System.out.println("4. Vote game");
            System.out.println("5. Exit");
            System.out.print("Select (1-5): ");

            String choice = scanner.nextLine().trim();

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

        if (betCategory.isEmpty()) betCategory = "ALL";
        if (riskLevel.isEmpty()) riskLevel = "ALL";
        if (minStars.isEmpty()) minStars = "0";

        try (
            Socket socket = new Socket(ClientHANDLER_IP, ClientHandler_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(Message.SEARCH);
            out.println(betCategory);
            out.println(riskLevel);
            out.println(minStars);

            List<GameSearchResult> results = new ArrayList<>();

            while (true) {
                String firstField = in.readLine();

                if (firstField == null || firstField.equals(Message.END)) {
                    break;
                }

                GameSearchResult game = new GameSearchResult();
                game.gameName = firstField;
                game.providerName = in.readLine();
                game.gameLogo = in.readLine();
                game.stars = in.readLine();
                game.noOfVotes = in.readLine();
                game.minBet = in.readLine();
                game.maxBet = in.readLine();
                game.riskLevel = in.readLine();
                game.betCategory = in.readLine();
                game.jackpot = in.readLine();

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

    private static void handleAddBalance(Scanner scanner, String playerId) {
        System.out.println();
        System.out.println("----- ADD BALANCE -----");

        System.out.print("Give amount to add: ");
        String amount = scanner.nextLine().trim();

        try (
            Socket socket = new Socket(ClientHANDLER_IP, ClientHandler_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(Message.ADD_BALANCE);
            out.println(playerId);
            out.println(amount);

            String status = in.readLine();

            if (status != null && status.equals(Message.OK)) {
                System.out.println("Balance added successfully!");
            } else {
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
            Socket socket = new Socket(ClientHANDLER_IP, ClientHandler_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(Message.PLAY);
            out.println(gameName);
            out.println(playerId);        
            out.println(betAmount);

            String status = in.readLine();

            if (status == null || !status.equals(Message.OK)) {
                System.out.println("Something went wrong: " + status);
                return;
            }

            String resultStr = in.readLine();
            String type = in.readLine();

            double result = Double.parseDouble(resultStr);

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

    private static void handleVote(Scanner scanner) {
        System.out.println();
        System.out.println("----- VOTE GAME -----");

        System.out.print("Give game name: ");
        String gameName = scanner.nextLine().trim();

        System.out.print("Give stars (1-5): ");
        String stars = scanner.nextLine().trim();

        try (
            Socket socket = new Socket(ClientHANDLER_IP, ClientHandler_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(Message.VOTE);
            out.println(gameName);
            out.println(stars);

            String status = in.readLine();
            System.out.println("The status of your vote is: " + status);

        } catch (Exception e) {
            System.out.println("Error sto VOTE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class GameSearchResult {
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

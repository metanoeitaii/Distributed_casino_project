package manager; 
import java.io.*;
import java.net.*;
import java.util.Scanner;
import common.Message;
import com.google.gson.Gson;
import java.nio.file.*;
import common.Game;
public class Manager {
    private static final String Master_Ip = "localhost"; //ip masterserver
    private static final int Master_port = 1000;        //port masterserver
    public static void main(String[] args) {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("KSEKINAEI O MANAGER");
        while (true) {
            //emfanizei menu epilogwn
            System.out.println("MENU:");
            System.out.println("1. Add Game (ADD_GAME)");
            System.out.println("2. Remove Game (REMOVE_GAME)");
            System.out.println("3. Update Risk (UPDATE_RISK)");
            System.out.println("4. Profit/Loss per Provider");
            System.out.println("5. Profit/Loss per Player");
            System.out.println("6. Profit/Loss per Game");
            System.out.println("7. Exit");
            System.out.print("Choose an option (1-7): ");
            String epilogi = scanner.nextLine();
            if(epilogi.equals("1")){
                //diabazei json kai stelnei ston Master
                System.out.println("Dose to path tou JSON");
                String filename = scanner.nextLine();
                
            try (Socket socket = new Socket(Master_Ip, Master_port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {  
                    //diabasma json arxeiou 
                String json = new String(Files.readAllBytes(Paths.get(filename)));
                 Gson gson = new Gson();
                Game game = gson.fromJson(json, Game.class);
                //stelnei dedomena ston master 
                out.println(Message.ADD_GAME);
               out.println(game.getGameName());
               out.println(game.getProviderName());
               out.println(game.getStars());
               out.println(game.getNoOfVotes());
               out.println(game.getGameLogo());
               out.println(game.getMinBet());
               out.println(game.getMaxBet());
               out.println(game.getRiskLevel());
               out.println(game.getHashKey());
                String apantisi = in.readLine();
                System.out.println("Apantisi:"+ apantisi);

            

    } catch (Exception e) {
        System.out.println("Error: "+ e.getMessage());
    }   
}  else if (epilogi.equals("2")) {
                System.out.print("Dose to onoma tou paixnidiou: ");
                String gameName = scanner.nextLine();
                
                try (Socket socket = new Socket(Master_Ip, Master_port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    
                    out.println(Message.REMOVE_GAME);
                    out.println(gameName);
                    
                    String apantisi = in.readLine();
                    System.out.println("Apantisi: " + apantisi);
                    
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
                else if (epilogi.equals("3")) {
                System.out.print("Dose to onoma tou paixnidiou: ");
                String gameName = scanner.nextLine();
                System.out.print("Dose neo risk (low/medium/high): ");
                String newRisk = scanner.nextLine();
                
                try (Socket socket = new Socket(Master_Ip, Master_port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    
                    out.println(Message.UPDATE_RISK);
                    out.println(gameName);
                    out.println(newRisk);
                    
                    String apantisi = in.readLine();
                    System.out.println("Apantisi: " + apantisi);
                    
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
         }       }else if (epilogi.equals("4") || epilogi.equals("5") || epilogi.equals("6")) {
                String mapType;
                if (epilogi.equals("4")) mapType = "PROVIDER";
                else if (epilogi.equals("5")) mapType = "PLAYER";
                else mapType = "GAME";

                try (Socket socket = new Socket(Master_Ip, Master_port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                   
                    out.println(Message.MAP);
                    out.println(mapType);
                    String line = in.readLine();
                    while (line != null && !line.equals(Message.END)) {
                        System.out.println(line);
                        line = in.readLine();
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

}            else if (epilogi.equals("7")) {
    //exodos apo to programma
                System.out.println("EXODOS APO TO MENU");
                break;
    }
        }
        scanner.close();
    
}
    }

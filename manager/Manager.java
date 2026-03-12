package manager; 
import java.io.*;
import java.net.*;
import java.util.Scanner;
import common.Message;
public class Manager {
    private static final String Master_Ip = "localhost";
    private static final int Master_port = 1000;
    public static void main(String[] args) {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("KSEKINAEI O MANAGER");
        while (true) {
            System.out.println("MENU:");
            System.out.println("1. Prosthiki paixnidiou (ADD_GAME");
            System.out.println("2.Afairesi Paixnidiou (REMOVE_GAME");
            System.out.println("3.ALLAGH RISKOU(UPDATE_RISK");
            System.out.println("4.EXIT");
            System.out.println("PARAKALW EPILEXTE MIA APO TIS 4 EPILOGES(1-4):");
            String epilogi = scanner.nextLine();
            if(epilogi.equals("1")){
                System.out.println("Dose to path tou JSON");
                String filename = scanner.nextLine();
                
            try (Socket socket = new Socket(Master_Ip, Master_port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {   
                out.println(Message.ADD_GAME);
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
                }
}else if (epilogi.equals("4")) {
                System.out.println("EXODOS APO TO MENU");
                break;
    }
        }
        scanner.close();
    
}
    }

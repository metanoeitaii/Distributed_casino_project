package master;
import java.io.*;
import java.net.*;
import java.util.*;
import common.Message;
public class MasterServer {
    private static int clientport = 1000;
    private static int workerport = 1001;
    private static List<Socket> workers = new ArrayList<>();
    

    
    public static void main(String[] args) {
       try{                                                         
        // to try to bazw epeidh petaei Exception sto object
    ServerSocket serverSocket = new ServerSocket(clientport);
    ServerSocket serverSocket2 = new ServerSocket(workerport);
    System.out.println("Perimenoume gia clients sto port 1000");
    System.out.println("Perimenoume gia workers sto port 1001");
    Thread workerThread = new Thread(){
        
        public void run(){
            try{
                //loop gia na dexetai pollous workers 
                while (true) {
                    Socket sock1 = serverSocket2.accept();
                    System.out.println("O worker sindtethike");
                    //apothikeuw worker
                    workers.add(sock1);
                    
                }
            }
                 catch (Exception e) {
                    System.out.println("ERROR");
                    e.printStackTrace();
                }
        }
    };
        workerThread.start();
    //loop gia na dexetai pollous clients
    while (true) {
        Socket sock2 =serverSocket.accept();
        System.out.println("O client sindethike");
        BufferedReader in = new BufferedReader(
            new InputStreamReader(sock2.getInputStream())
        );
        String entoli = in.readLine();
        System.out.println("Entoli"+ entoli);
        if(entoli.equals(Message.ADD_GAME)){
            System.out.println("H entoli pou elava einai ADD_GAME");
            String GameName = in.readLine();
            String providerName = in.readLine();
            String Stars = in.readLine();
            String noOfVotes = in.readLine();
            String gameLogo = in.readLine();
            String minBet = in.readLine();
            String maxBet = in.readLine();
            String riskLevel = in.readLine();
            String HashKey = in.readLine();
            //upologizoume se poio worker paei mesw hash
            char protogramma = GameName.charAt(0);
            int workerthesi= protogramma % workers.size();
            System.out.println("To game "+ GameName + "paei ston worker "+ workerthesi);

        }else if(entoli.equals(Message.REMOVE_GAME)){
            System.out.println("H entoli pou elaba einai REMOVE_GAME");
             String GameName = in.readLine();
             //upologizoume se poio worker paei mesw hash
            char protogramma = GameName.charAt(0);
            int workerthesi= protogramma % workers.size();
            System.out.println("To game "+ GameName + "paei ston worker "+ workerthesi);

        }   // o client einai player 
        else if(entoli.equals(Message.SEARCH)){

            System.out.println("H entoli pou elava einai SEARCH");
            // o client einai player 
        }else if(entoli.equals(Message.RATE)){
             String GameName = in.readLine();
            String Stars = in.readLine();
            System.out.println("H entoli pou elava einai Rate");
              //upologizoume se poio worker paei mesw hash
            char protogramma = GameName.charAt(0);
            int workerthesi= protogramma % workers.size();
            System.out.println("To game "+ GameName + "paei ston worker "+ workerthesi);

            // o client einai player 
        }else if(entoli.equals(Message.ADD_BALANCE)){
            System.out.println("H entoli pou elava einai ADD_BALANCE");

        }else if(entoli.equals(Message.UPDATE_RISK)){
            System.out.println("H entoli pou elava einai UPDATE_RISK");
              String GameName = in.readLine();
            String providerName = in.readLine();
            String Stars = in.readLine();
            String noOfVotes = in.readLine();
            String gameLogo = in.readLine();
            String minBet = in.readLine();
            String maxBet = in.readLine();
            String riskLevel = in.readLine();
            String HashKey = in.readLine();
            System.out.println("H entoli pou elava einai Rate");
              //upologizoume se poio worker paei mesw hash
            char protogramma = GameName.charAt(0);
            int workerthesi= protogramma % workers.size();
            System.out.println("To game "+ GameName + "paei ston worker "+ workerthesi);


        }   // o client einai player 
        else if(entoli.equals(Message.PLAY)) 
            {
            System.out.println("H entoli pou elaba einai Play");
              String GameName = in.readLine();
            System.out.println("H entoli pou elava einai Rate");
              //upologizoume se poio worker paei mesw hash
            char protogramma = GameName.charAt(0);
            int workerthesi= protogramma % workers.size();
            System.out.println("To game "+ GameName + "paei ston worker "+ workerthesi);


        }
        
    }
    
}catch(Exception e){
    System.out.println("Error");
    e.printStackTrace();
}
    
}
}


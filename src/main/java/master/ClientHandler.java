package master;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import common.Message;

public class ClientHandler extends Thread {
    private Socket sock2;
    private List<String> workerHosts;
    private List<Integer> workerPorts;

    public ClientHandler(Socket sock2, List<String> workerHosts, List<Integer> workerPorts) {
        this.sock2 = sock2;
        this.workerHosts = workerHosts;
        this.workerPorts = workerPorts;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock2.getInputStream())
            );
            String entoli = in.readLine();
            System.out.println("Entoli" + entoli);

            if (entoli.equals(Message.ADD_GAME)) {
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
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);
                // new Socket gia worker 
                Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                //stelnw  dedomena se workerhandler 
                PrintWriter workerOut = new PrintWriter(workerSocket.getOutputStream(), true);
                workerOut.println(Message.ADD_GAME);
                workerOut.println(GameName);
                workerOut.println(providerName);
                workerOut.println(Stars);
                workerOut.println(noOfVotes);
                workerOut.println(gameLogo);
                workerOut.println(minBet);
                workerOut.println(maxBet);
                workerOut.println(riskLevel);
                workerOut.println(HashKey);
                BufferedReader workerIn = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
                String apantisii = workerIn.readLine();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisii);
                //kleinw worker socket
                workerSocket.close();

            } else if (entoli.equals(Message.REMOVE_GAME)) {
                System.out.println("H entoli pou elaba einai REMOVE_GAME");
                String GameName = in.readLine();
                //upologizoume se poio worker paei mesw hash
                char protogramma = GameName.charAt(0);
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);
                  // new Socket gia worker 
                Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                 //stelnw  dedomena se workerhandler 
                PrintWriter workerOut = new PrintWriter(workerSocket.getOutputStream(), true);
                workerOut.println(Message.REMOVE_GAME);
                workerOut.println(GameName);
                BufferedReader workerIn = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
                String apantisi = workerIn.readLine();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                //kleinw worker socket 
                workerSocket.close();
            }   // o client einai player 
            else if (entoli.equals(Message.SEARCH)) {
                System.out.println("H entoli pou elava einai SEARCH");
                // o client einai player

            } else if (entoli.equals(Message.VOTE)) {
                String GameName = in.readLine();
                String Stars = in.readLine();
                System.out.println("H entoli pou elava einai Rate");
                //upologizoume se poio worker paei mesw hash
                char protogramma = GameName.charAt(0);
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);\
                //new socket gia worker
                Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                 //stelnw  dedomena se workerhandler 
                PrintWriter workerOut = new PrintWriter(workerSocket.getOutputStream(), true);
                workerOut.println(Message.VOTE);
                workerOut.println(GameName);
                workerOut.println(Stars);
                 BufferedReader workerIn = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
                String apantisi = workerIn.readLine();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                //kleinw worker socket 
                workerSocket.close();
                // o client einai player 
            } else if (entoli.equals(Message.ADD_BALANCE)) {
                System.out.println("H entoli pou elava einai ADD_BALANCE");

            } else if (entoli.equals(Message.UPDATE_RISK)) {
                System.out.println("H entoli pou elava einai UPDATE_RISK");
                String GameName = in.readLine();
                String riskLevel = in.readLine();
                System.out.println("H entoli pou elava einai Rate");
                //upologizoume se poio worker paei mesw hash
                char protogramma = GameName.charAt(0);
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);
                Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                PrintWriter workerOut = new PrintWriter(workerSocket.getOutputStream(), true);
                workerOut.println(Message.UPDATE_RISK);
                workerOut.println(GameName);
                workerOut.println(riskLevel);
                BufferedReader workerIn = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
                String apantisi = workerIn.readLine();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                workerSocket.close();

            }   // o client einai player 
            else if (entoli.equals(Message.PLAY)) {
                System.out.println("H entoli pou elaba einai Play");
                String GameName = in.readLine();
                String playerId = in.readLine();
                String betAmount = in.readLine();
                System.out.println("H entoli pou elava einai Rate");
                //upologizoume se poio worker paei mesw hash
                char protogramma = GameName.charAt(0);
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
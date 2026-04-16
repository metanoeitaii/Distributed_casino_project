package master;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket; 
import java.util.ArrayList;
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
                ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                workerOut.writeObject(Message.ADD_GAME);
                workerOut.writeObject(GameName);
                workerOut.writeObject(providerName);
                workerOut.writeObject(Stars);
                workerOut.writeObject(noOfVotes);
                workerOut.writeObject(gameLogo);
                workerOut.writeObject(minBet);
                workerOut.writeObject(maxBet);
                workerOut.writeObject(riskLevel);
                workerOut.writeObject(HashKey);
                workerOut.flush();
               ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                String apantisii = (String)workerIn.readObject();
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
                ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                workerOut.writeObject(Message.REMOVE_GAME);
                workerOut.writeObject(GameName);
                workerOut.flush();
               ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                String apantisi = (String)workerIn.readObject();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                //kleinw worker socket 
                workerSocket.close();
            }   // o client einai player 
            else if (entoli.equals(Message.SEARCH)) {
                System.out.println("H entoli pou elava einai SEARCH");
                Socket searchReducerSocket = new Socket("localhost", 2000);
                ObjectOutputStream outToSearchReducer = new ObjectOutputStream(searchReducerSocket.getOutputStream());
                outToSearchReducer.writeObject(workerHosts.size()); 
                outToSearchReducer.flush();
                // o client einai player
                String betCategory = in.readLine();
                String riskLevel = in.readLine();
                String minStars = in.readLine();
                SearchState state = new SearchState(workerHosts.size());
                   // jekinaw ena thread gia kathe worker
                  
                for (int i = 0; i < workerHosts.size(); i++) {
                    SearchWorkerThread thread = new SearchWorkerThread(
                        workerHosts.get(i), workerPorts.get(i),
                        betCategory, riskLevel, minStars, state
                    );
                    thread.start();
                
                }
               state.perimenw_finishworkers();

                // 1. Διάβασε τη λίστα από τον Reducer
                ObjectInputStream inFromReducer = new ObjectInputStream(searchReducerSocket.getInputStream());
                List<String> gamesFound = (List<String>) inFromReducer.readObject();

                // 2. ΟΡΙΣΜΟΣ του clientout (εδώ ήταν το λάθος)
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(), true);

                // 3. Στείλε τα αποτελέσματα στον Player
                for (String game : gamesFound) {
                    clientout.println(game);
                }

                // 4. Σήμα τέλους και κλείσιμο
                clientout.println(Message.END);
                searchReducerSocket.close();
            }else if (entoli.equals(Message.MAP)){
                System.out.println("H entoli pou elava einai MAP");
                String typosMap = in.readLine();
                //sunedomai me reducer 
                Socket reducerSocket = new Socket("localhost",2000);
                ObjectOutputStream reducerout = new ObjectOutputStream(reducerSocket.getOutputStream());
                reducerout.writeObject(workerHosts.size());
                reducerout.flush();

                //stelnw map se olous tous workers 
                for(int i=0; i<workerHosts.size(); i++){
                    Socket workerSocket = new Socket(workerHosts.get(i),workerPorts.get(i));
                       ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                        workerOut.writeObject(Message.MAP);
                        workerOut.writeObject(typosMap);
                        workerOut.flush();
                        ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                        String apantisi = (String) workerIn.readObject();
                        workerSocket.close();
                }   // diabazw apotelesmata apo reducer
                        ObjectInputStream reducerIn = new ObjectInputStream(reducerSocket.getInputStream());
                        PrintWriter clientout = new PrintWriter(sock2.getOutputStream(), true);
                 Object obj = reducerIn.readObject();
                    while (!obj.equals(Message.END)) {
                        String key = (String) obj;
                        Double value = (Double) reducerIn.readObject();
                        clientout.println(key + ": " + value);
                        obj = reducerIn.readObject();
                    }
                    clientout.println(Message.END);

                    reducerSocket.close();

            }



                
                else if (entoli.equals(Message.VOTE)) {
                String GameName = in.readLine();
                String Stars = in.readLine();
                System.out.println("H entoli pou elava einai Rate");
                //upologizoume se poio worker paei mesw hash
                char protogramma = GameName.charAt(0);
                int workerthesi = protogramma % workerHosts.size();
                System.out.println("To game " + GameName + "paei ston worker " + workerthesi);
                //new socket gia worker
                Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                 //stelnw  dedomena se workerhandler 
                 ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                workerOut.writeObject((Message.VOTE));
                workerOut.writeObject((GameName));
                
                workerOut.writeObject((Stars));
                workerOut.flush();
                  ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                String apantisi = (String)workerIn.readObject();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                //kleinw worker socket 
                workerSocket.close();
                // o client einai player 
            } else if (entoli.equals(Message.ADD_BALANCE)) {
                System.out.println("H entoli pou elava einai ADD_BALANCE");
                String PlayerId = in.readLine();
                String amount = in.readLine();
                for(int i=0; i<workerHosts.size(); i++){
                     Socket workerSocket = new Socket(workerHosts.get(i), workerPorts.get(i));
                    ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                     workerOut.writeObject(Message.ADD_BALANCE);
                     workerOut.writeObject(PlayerId);
                    workerOut.writeObject(amount);
                    workerOut.flush();
                    ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                    String apantisi = (String) workerIn.readObject();
                    workerSocket.close();
                } PrintWriter clientout = new PrintWriter(sock2.getOutputStream(), true);
                clientout.println(Message.OK);
                

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
                ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                workerOut.writeObject((Message.UPDATE_RISK));
                workerOut.writeObject((GameName));
                workerOut.writeObject((riskLevel));
                workerOut.flush();
                ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                String apantisi = (String) workerIn.readObject();
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
                 Socket workerSocket = new Socket(workerHosts.get(workerthesi),workerPorts.get(workerthesi));
                ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
                workerOut.writeObject((Message.PLAY));
                workerOut.writeObject((playerId));   // prwta playerId
                workerOut.writeObject((GameName));   // meta GameName
                workerOut.writeObject((betAmount));
                workerOut.flush();
               ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());
                String apantisi = (String) workerIn.readObject();           //kanw casting 
                double apantisi2 = (double) workerIn.readObject();
                String apantisi3 = (String) workerIn.readObject();
                PrintWriter clientout = new PrintWriter(sock2.getOutputStream(),true);
                clientout.println(apantisi);
                clientout.println(String.valueOf(apantisi2));
                clientout.println(apantisi3);
                workerSocket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

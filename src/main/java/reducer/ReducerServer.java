package reducer;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import common.Message;


public class ReducerServer {

    private int reducerPort;
    
    public ReducerServer(int reducerPort) {
        this.reducerPort = reducerPort;
    }
    
    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(reducerPort)) {
            System.out.println("Reducer listening on port " + reducerPort);

            while (true) {
                System.out.println("Waiting for master...");

                Socket masterSocket = serverSocket.accept();
                System.out.println("Master connected");

                ObjectInputStream masterIn = new ObjectInputStream(masterSocket.getInputStream()); //diavazei apo ton master posoi workers 8a synde8oun
                int expectedWorkers = (Integer) masterIn.readObject();
                String mode = (String) masterIn.readObject(); //MAP/SEARCH

                ReducerState state = new ReducerState(expectedWorkers);//koino state pou krataei ta apotelesmata kai tis plirofories gia tous workers

                for (int i = 0; i < expectedWorkers; i++) {
                    Socket workerSocket = serverSocket.accept();
                    System.out.println("Worker connected to reducer");

                    Thread t = new Thread(new ReducerWorkerHandler(workerSocket, state, mode)); //thread gia kathe worker pou syndeetai
                    t.start();
                }

                state.waitUntilAllWorkersFinish(); //mplokarei mexri na teleiwsoun oloi oi workers

                ObjectOutputStream masterOut  = new ObjectOutputStream(masterSocket.getOutputStream());
                if (mode.equals("MAP")){
                    //stelnei key + value
                    Map<String, Double> finalTotal = state.getTotalsCopy();
                    for (String key : finalTotal.keySet()) {
                        masterOut.writeObject(key);
                        masterOut.writeObject(finalTotal.get(key));
                    } 
                }else if (mode.equals("SEARCH")){
                    //stelnei keys (gameName)
                    for (String[] gameData : state.getSearchResultsCopy()){
                        for (String field : gameData){
                            masterOut.writeObject(field);
                        }                 
                    }
                }

                masterOut.writeObject(Message.END);
                masterOut.flush();
                System.out.println("Results sent to master");
                masterSocket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: Please provide the Reducer port as an argument!");
            return;
        }
        int reducerPort = Integer.parseInt(args[0]);
        ReducerServer server = new ReducerServer(reducerPort);
        server.start();
    }
}

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
                
                ReducerState state = new ReducerState(expectedWorkers);//koino state pou krataei ta apotelesmata kai tis plirofories gia tous workers

                for (int i = 0; i < expectedWorkers; i++) {
                    Socket workerSocket = serverSocket.accept();
                    System.out.println("Worker connected to reducer");

                    Thread t = new Thread(new ReducerWorkerHandler(workerSocket, state)); //thread gia kathe worker pou syndeetai
                    t.start();
                }

                state.waitUntilAllWorkersFinish(); //mplokarei mexri na teleiwsoun oloi oi workers

                // stelnei ta results ston master 
                Map<String, Double> finalTotal = state.getTotalsCopy();
                ObjectOutputStream masterOut  = new ObjectOutputStream(masterSocket.getOutputStream());
                for (String key : finalTotal.keySet()) {
                    masterOut.writeObject(key);
                    masterOut.writeObject(finalTotal.get(key));
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

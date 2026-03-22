package reducer;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import common.Message;


public class ReducerServer {

    private int reducerPort;//port pou akouei o reducer gia na syndethei me tous workers
    
    public ReducerServer(int reducerPort) {
        this.reducerPort = reducerPort;
    }
    
    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(reducerPort)) {//anoigei port
            System.out.println("Reducer listening on port " + reducerPort);

            while (true) {//den kleinei meta apo kathe request, menei anoixtos o server gia seiriaka polla request
                System.out.println("Waiting for master...");

                Socket masterSocket = serverSocket.accept();//syndeetai o master
                System.out.println("Master connected");

                ObjectInputStream masterIn = new ObjectInputStream(masterSocket.getInputStream());//diavazei apo ton master to expectedWorkers
                int expectedWorkers = (Integer) masterIn.readObject();//px 3 workers 
                
                ReducerState state = new ReducerState(expectedWorkers);//koino state pou krataei ta apotelesmata kai tis plirofories gia tous workers

                for (int i = 0; i < expectedWorkers; i++) {
                    Socket workerSocket = serverSocket.accept();//worker syndeetai me ton reducer
                    System.out.println("Worker connected to reducer");

                    Thread t = new Thread(new ReducerWorkerHandler(workerSocket, state));//dimiourgeitai ena thread gia kathe worker pou syndeetai
                    t.start();
                }

                state.waitUntilAllWorkersFinish();//mplokarei mexri na teleiwsoun oloi oi workers

                Map<String, Double> finalTotal = state.getTotalsCopy();//pairnei ta apotelesmata apo to state

                ObjectOutputStream masterOut  = new ObjectOutputStream(masterSocket.getOutputStream());//gia na steilei ta apotelesmata ston master

                for (String key : finalTotal.keySet()) {//stelnei jexwrista to key kai to value ston master gia kathe entry sto finalTotals
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
        int reducerPort = 2000;

        ReducerServer server = new ReducerServer(reducerPort);
        server.start();
    }
}

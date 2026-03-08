import java.net.*; //ServerSocket, Socket
import java.io.*; // gia IOException

//TCP SERVER WORKER - dexetai connections apo master
public class Worker{
    private int port;
    private WorkerStorage storage;

    public Worker(int port){
        this.port = port; //port poy akoyei o worker 
        this.storage = new WorkerStorage(); //to in-memory storage poy ekana , ftiaxnw ena adeio
    }

    public void start(){
        //kleinei to serverSocket otan teleiwsei
        try (ServerSocket serverSocket = new ServerSocket(port)){ //anoigw akroath (serverSocket) sto port 
            System.out.println("Worker is listening on port " + port);
            while(true){ //o worker akouei synexeia gia nea connections 
                Socket clientSocket = serverSocket.accept(); //perimenei mexri o master na synde8ei, mplokarei mexri na er8ei connection
                WorkerHandler handler = new WorkerHandler(clientSocket, storage); //otan er8ei connection, neo thread gia diaxeirhsh 
                Thread t = new Thread(handler); //neo thread gia diaxeirhsh
                t.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //gia an trexoume ton worker apo cmd 
    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Worker worker = new Worker(port);
        worker.start();
    }
}
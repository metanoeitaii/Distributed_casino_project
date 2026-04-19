package worker;

import java.net.*; 
import java.io.*; 
import common.*;
import srg.*;

//TCP SERVER WORKER - dexetai connections apo master
public class Worker{
    private int port;
    private WorkerStorage storage;
    private String srgHost;     
    private int srgPort;
    private String reducerHost;
    private int reducerPort;

    public Worker(int port, String srgHost, int srgPort, String reducerHost, int reducerPort){
        this.port = port; 
        this.srgHost = srgHost;
        this.srgPort = srgPort; 
        this.reducerHost = reducerHost;
        this.reducerPort = reducerPort;
        this.storage = new WorkerStorage(); 

    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)){ 
            System.out.println("Worker is listening on port " + port);
            while(true){ 
                Socket clientSocket = serverSocket.accept(); 
                // neo thread gia ka8e connection
                WorkerHandler handler = new WorkerHandler(clientSocket, storage, srgHost, srgPort, reducerHost, reducerPort);   
                Thread t = new Thread(handler); 
                t.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        String srgHost = args[1];       
        int srgPort = Integer.parseInt(args[2]); 
        String reducerHost = args[3];
        int reducerPort = Integer.parseInt(args[4]); 
        Worker worker = new Worker(port, srgHost, srgPort, reducerHost, reducerPort);
        worker.start();
    }
}

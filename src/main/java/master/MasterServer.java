package master;
import java.io.*;
import java.net.*;
import java.util.*;
import common.Message;
public class MasterServer {
    private static int clientport = 1000;
    private static List<Socket> workers = new ArrayList<>();
    

    
    public static void main(String[] args) {
       try{                                                         
        // to try to bazw epeidh petaei Exception sto object
    ServerSocket serverSocket = new ServerSocket(clientport);
   
    System.out.println("Perimenoume gia clients sto port 1000");
    Socket worker1 = new Socket("192.168.bla.bla",1001);
    workers.add(worker1);
    System.out.println("Sundesh sto worker 1");
    //loop gia na dexetai pollous clients
    while (true) {
        Socket sock2 =serverSocket.accept();
        System.out.println("O client sindethike");
        Clienthandler handler = new Clienthandler(sock2,workers);
        Thread t = new Thread();
        t.start();
                
                
                
          

        }
        
    
    
}catch(Exception e){
    System.out.println("Error");
    e.printStackTrace();
}
    
}


}
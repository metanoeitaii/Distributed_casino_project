package master;
    import java.io.*;
    import java.net.*;
    import java.util.*;
    import common.Message;
    public class MasterServer {
        private static int clientport = 1000;
        private static List<String> workerHosts = new ArrayList<>();
        private static List<Integer> workerPorts = new ArrayList<>();
       
       
        public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(clientport)){                                                        
   
            System.out.println("Perimenoume gia clients sto port " + clientport);
            for (int i = 0; i < args.length; i += 2) {
                workerHosts.add(args[i]);
                workerPorts.add(Integer.parseInt(args[i + 1]));
                System.out.println("Connected to worker on " + args[i] + ":" + args[i + 1]);
            }
           
            //loop gia na dexetai pollous clients
            while (true) {
                Socket sock2 =serverSocket.accept();
                System.out.println("O client sindethike");
                ClientHandler handler = new ClientHandler(sock2,workerHosts,workerPorts);
                handler.start();      
            }    
    }catch(Exception e){
        System.out.println("Error");
        e.printStackTrace();
    }
       
    }
    }
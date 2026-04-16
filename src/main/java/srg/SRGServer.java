    package srg;

    import java.net.*; //gia socket 
    import java.io.*;

    public class SRGServer {

        public static void main(String[] args) {
           if (args.length < 1) {
            System.out.println("Sfalma: Parakalw dwste to port tou SRG Server ws orisma!");
            System.out.println("Paradeigma: java srg.SRGServer 5000");
            return; // Σταματάει το πρόγραμμα εδώ αν δεν δώσεις port
        }
        
        // Παίρνει το port αυστηρά από το τερματικό
        int port = Integer.parseInt(args[0]);
            try (ServerSocket serverSocket = new ServerSocket(port)) {//gia na perimenei na syndethei o worker
                System.out.println("SRG Server started on port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();//otan syndetai enas worker, to accept epistrefei ena socket gia thn syndesh
                    System.out.println("Worker connected");

                    new Thread(new SRGWorkerHandler(socket)).start();//gia na dimiourghsei ena neo thread gia kathe worker pou syndetai
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

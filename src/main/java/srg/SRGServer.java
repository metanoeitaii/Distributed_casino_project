    package srg;

    import java.net.*; 
    import java.io.*;

    public class SRGServer {

        public static void main(String[] args) {
           if (args.length < 1) {
            System.out.println("Error: Please provide the SRG Server port as an argument!");
            return; 
        }

        int port = Integer.parseInt(args[0]);
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("SRG Server started on port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Worker connected");

                    new Thread(new SRGWorkerHandler(socket)).start(); //neo thread gia kathe worker pou syndeetai
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

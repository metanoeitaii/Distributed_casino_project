package srg;

import java.net.*; //gia socket 
import java.io.*;

public class SRGServer {

    public static void main(String[] args) {
        int port = 5000;

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

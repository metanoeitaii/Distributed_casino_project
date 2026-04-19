package srg;

import java.net.*;
import java.io.*; 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 


//syndeetai me ton SRG server kai gemisei buffer me random numbers 
public class SRGClient implements Runnable{
    private String host;
    private int port;
    private String secret; // koino hashkey me ton srg gia epalh8eysh
    private RandomNumberBuffer buffer;

    public SRGClient(String host, int port, String secret, RandomNumberBuffer buffer){
        this.host = host;
        this.port = port;
        this.secret = secret;
        this.buffer = buffer;
    }


    @Override
    public void run(){
        try(
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            out.writeObject(secret); //otan ginei connection, stelnw to hashkey gia na jerei o SRG gia poio game kanei numbers
            out.flush();

            while(true){ 
                String numberStr = (String) in.readObject();
                String receivedHash = (String) in.readObject(); 

                if(numberStr == null || receivedHash == null){
                    break;
                }

                int number = Integer.parseInt(numberStr);

                // epalh8eysh akeraiothtas me SHA-256
                String expectedHash = sha256(numberStr + secret); //ypologizoume hash me to number kai to secret 
                if(expectedHash.equals(receivedHash)){ //sygkrinw me ayto poy esteile o SRG 
                    buffer.produce(number); //an tairiazoun bazw number sto buffer gia to game 
                }else{
                    System.out.println("WARNING: HASH MISMATCH! DISCARDING NUMBER.");

                }
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    //metatroph string se sha-256 hash
    private String sha256(String input){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); 
            byte[] hash = digest.digest(input.getBytes()); 
            StringBuilder hexString = new StringBuilder();
            for(byte b : hash){ //metatroph ka8e byte se hex 
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1){ 
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();  
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
}

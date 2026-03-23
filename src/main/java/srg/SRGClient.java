package srg;

import java.net.*; //gia socket 
import java.io.*; 
import java.security.MessageDigest; //gia SHA-256
import java.security.NoSuchAlgorithmException; //gia to exception toy SHA-256


//syndeetai me ton SRG server kai gemisei buffer me random numbers 
public class SRGClient implements Runnable{
    private String host; //host SRG server
    private int port; //port SRG server
    private String secret; //hashkey game, koino secret me SRG
    private RandomNumberBuffer buffer; //to buffer pou gemizei

    public SRGClient(String host, int port, String secret, RandomNumberBuffer buffer){
        this.host = host;
        this.port = port;
        this.secret = secret;
        this.buffer = buffer;
    }


    @Override
    public void run(){
        try(
            Socket socket = new Socket(host, port); //syndesh me SRG server 
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            out.writeObject(secret); //otan ginei connection, stelnw to hashkey gia na jerei o SRG gia poio game kanei numbers
            out.flush();

            while(true){ //trexei synexeia, pairnei ari8mous kai gemizei to buffer
                String numberStr = (String) in.readObject(); //diabazei to random number poy esteile o SRG
                String receivedHash = (String) in.readObject(); //diabazei to hash poy esteile o SRG

                //an to connection kleisei, stamatame 
                if(numberStr == null || receivedHash == null){
                    break;
                }

                int number = Integer.parseInt(numberStr);

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
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); //pairnei sha algorith apo java 
            byte[] hash = digest.digest(input.getBytes()); //ypologizei hash -> apotelesma pinakas apo bytes 
            StringBuilder hexString = new StringBuilder();
            for(byte b : hash){ //metatroph ka8e byte se hex 
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1){ //ka8e byte prepei na einai 2 xarakthres, an einai 1 pros8etoume 0 mprosta 
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString(); //epistrofh telikoy hash ws string 
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
}

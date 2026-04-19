package srg;

import java.net.*; 
import java.io.*; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.util.Random;

// xeirizetai th syndesh me enan worker , stelnei random numbers me sha-256 epalh8eysh
public class SRGWorkerHandler implements Runnable { 

    private Socket socket;

    public SRGWorkerHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {

        try (                   
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {

            String secret = (String) in.readObject(); //secret pou stelnei o worker gia to sygkekrimeno game
            System.out.println("Secret received: " + secret);

            Random random = new Random();

            while (true) { 

                int number = random.nextInt(100); // 0 ews 99

                String numberStr = String.valueOf(number);
                String hash = sha256(numberStr + secret); //security

                //stelnei jexwrista ton arithmo kai to hash
                out.writeObject(numberStr);
                out.writeObject(hash);
                out.flush();

                Thread.sleep(100);//perimenei 100 milsec prin steilei ton epomeno 
            }

        } catch (Exception e) {
            System.out.println("SRG connection closed: " + e.getMessage());
        }
    }

    // ypologizei sha-256 hash gia epalh8eysh akeraiothtas 
    private String sha256(String input) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1)
                    hexString.append('0');

                hexString.append(hex);
            }

            return hexString.toString(); 

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

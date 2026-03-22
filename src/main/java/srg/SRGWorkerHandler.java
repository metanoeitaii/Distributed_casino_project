package srg;

import java.net.*; //epikoinonia socket 
import java.io.*; 
import java.security.MessageDigest; //gia na ypologizei to SHA-256
import java.security.NoSuchAlgorithmException; //gia to exception toy SHA-256
import java.util.Random;

public class SRGWorkerHandler implements Runnable { //handler tou SRG server gia 1 Worker  //kathe fora poy worker syndeetai me srg dhmiougeitai ena antikeimeno kai trexei jexwristo thread

    private Socket socket;

    public SRGWorkerHandler(Socket socket) {
        this.socket = socket;//syndesh me sygkekrimeno worker
    }

    @Override
    public void run() {

        try (                   
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());//gia na steilei dedomena ston worker
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());//gia na diavasei dedomena apo ton worker
        ) {

            String secret = (String) in.readObject();//diavazei to secret pou stelnei o worker gia to sygkekrimeno game
            System.out.println("Secret received: " + secret);

            Random random = new Random();

            while (true) { //o handler stelnei synexeia random numbers mexri na kleisei h syndesh h mexri na ginei kapoio exception

                int number = random.nextInt(100);//dimiourgei random arithmo apo 0 ews 99

                String numberStr = String.valueOf(number);//metatroph int se String
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

    private String sha256(String input) {//ypologizei to SHA-256 hash enos string se hex   //security

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");//ergalio poy kanei SHA-256

            byte[] hash = digest.digest(input.getBytes()); //epistrefei array apo bytes

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {//metatrepei kathe byte se hex
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1)//an einai monopshfio bazei mprosta 0 gia na einai dipshfio
                    hexString.append('0');

                hexString.append(hex);
            }

            return hexString.toString(); //epistrefei to plhres SHA-256 hash ws string.

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

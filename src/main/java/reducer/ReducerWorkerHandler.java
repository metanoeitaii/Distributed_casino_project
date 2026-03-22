package reducer;

import java.net.Socket;
import java.io.ObjectInputStream;
import common.Message;

public class ReducerWorkerHandler implements Runnable {//

    private Socket socket;//gia na diavasei apo ton worker
    private ReducerState state;//to koino object pou krataei ta apotelesmata kai tis plirofories gia tous workers

    public ReducerWorkerHandler(Socket socket, ReducerState state) {
        this.socket = socket;
        this.state = state;
    }

    @Override
    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            while (true) {
                Object obj = in.readObject();

                if (obj instanceof String && ((String) obj).equals(Message.END)) {//otan o worker teleiwsei tha steilei to END gia na simainei oti teleiwse
                    boolean allFinished = state.workerFinished();//otan o worker teleiwsei, elegxoume an oloklirose oles oi workers

                    if (allFinished) {
                        System.out.println("All workers finished.");
                    }
                    break;
                }

                String key = (String) obj;//an den einai END einai key 
                Double value = (Double) in.readObject();

                state.addPair(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

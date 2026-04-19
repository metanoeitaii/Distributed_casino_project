package reducer;

import java.net.Socket;
import java.io.ObjectInputStream;
import common.Message;

// diaxeirhsh ths syndeshs me ena worker se jexwristo thread 
public class ReducerWorkerHandler implements Runnable {//

    private Socket socket;
    private ReducerState state;

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

                if (obj instanceof String && ((String) obj).equals(Message.END)) {
                    // eidopoiei to state oti aytos o worker teleiwse 
                    if (state.workerFinished()) {
                        System.out.println("All workers finished.");
                    }
                    break;
                }

                String key = (String) obj;
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

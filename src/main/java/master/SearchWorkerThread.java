package master;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import common.Message;

// stelnei search request se enan worker kai syllegei apotelesmata 
public class SearchWorkerThread  extends Thread{
    private String workerHost;
    private int workerport;
    private String BetCategory;
    private String riskLevel;
    private String minStars;
    private SearchState state;//koino object
      public SearchWorkerThread(String workerHost, int workerPort, String betCategory, String riskLevel, String minStars, SearchState state) {
        this.workerHost = workerHost;
        this.workerport = workerPort;
        this.BetCategory = betCategory;
        this.riskLevel = riskLevel;
        this.minStars = minStars;
        this.state = state;
    }
    public void run() {
    try {
        Socket workerSocket = new Socket(workerHost, workerport);
        ObjectOutputStream workerOut = new ObjectOutputStream(workerSocket.getOutputStream());
        ObjectInputStream workerIn = new ObjectInputStream(workerSocket.getInputStream());

        workerOut.writeObject(Message.SEARCH);
        workerOut.writeObject(BetCategory);
        workerOut.writeObject(riskLevel);
        workerOut.writeObject(minStars);
        //diabazw apotelesmata mexri END ws object
        Object obj = workerIn.readObject();
        while (!Message.END.equals(obj)) {
            state.addApotel(String.valueOf(obj));
            obj =  workerIn.readObject();
        }

        workerSocket.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    state.workerTeliose();
}

}

package reducer;

import java.net.Socket;
import java.io.ObjectInputStream;
import common.Message;

// diaxeirhsh ths syndeshs me ena worker se jexwristo thread 
public class ReducerWorkerHandler implements Runnable {//

    private Socket socket;
    private ReducerState state;
    private String mode; // MAP/SEARCH

    public ReducerWorkerHandler(Socket socket, ReducerState state, String mode) {
        this.socket = socket;
        this.state = state;
        this.mode = mode;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {
            while (true) {
                Object obj = in.readObject();

                if (obj instanceof String && ((String) obj).equals(Message.END)) {
                    // eidopoiei to state oti aytos o worker teleiwse 
                    if (state.workerFinished()) {
                        System.out.println("All workers finished.");
                    }
                    break;
                }

                if (mode.equals("MAP")){
                    String key = (String) obj;
                    Double value = (Double) in.readObject();
                    state.addPair(key, value);
                }else if(mode.equals("SEARCH")){
                    String gameName = (String) obj;
                    String providerName = (String) in.readObject();
                    String gameLogo = (String) in.readObject();
                    double stars = (double) in.readObject();
                    int noOfVotes = (int) in.readObject();
                    double minBet = (double) in.readObject();
                    double maxBet = (double) in.readObject();
                    String riskLevel = (String) in.readObject();
                    String betCategory = (String) in.readObject();
                    double jackpot = (double) in.readObject();

                    String[] gameData = {gameName, providerName, gameLogo,
                        String.valueOf(stars), String.valueOf(noOfVotes),
                        String.valueOf(minBet), String.valueOf(maxBet),
                        riskLevel, betCategory, String.valueOf(jackpot)};

                    state.addSearchResult(gameData);
                }
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

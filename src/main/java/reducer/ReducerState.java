package reducer;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// koino state metajy twn threads tou reducer 
public class ReducerState {

    private Map<String, Double> totals; // gia MAP 
    private List<String[]> searchResults; // gia SEARCH - ka8e stoixeio einai array me ola ta pedia
    private int expectedWorkers;
    private int finishedWorkers;

    public ReducerState(int expectedWorkers) {
        this.expectedWorkers = expectedWorkers;
        this.finishedWorkers = 0;
        this.totals = new HashMap<>();
        this.searchResults = new ArrayList<>();
    }

    // gia MAP
    public synchronized void addPair(String key, double value) {//prosthetei value sto key h dimiourgei neo key an den yparxei
        if (totals.containsKey(key)) {
            totals.put(key, totals.get(key) + value);
        } else {
            totals.put(key, value);
        }
    }

    // gia SEARCH 
    public synchronized void addSearchResult(String[] gameData){
        searchResults.add(gameData);
    }

    // eidopoiei otan teleiwsoyn oloi oi workers 
    public synchronized boolean workerFinished() {
        finishedWorkers++;

        if (finishedWorkers == expectedWorkers) {
            notifyAll();
            return true;
        }

        return false;
    }

    // mplokarei mexri na teleiwsoun oloi oi workers 
    public synchronized void waitUntilAllWorkersFinish() throws InterruptedException {
        while (finishedWorkers < expectedWorkers) {
            wait();
        }
    }

    // epistrefei antigrafo
    public synchronized Map<String, Double> getTotalsCopy() {
        return new HashMap<>(totals);
    }

    public synchronized List<String[]> getSearchResultsCopy() {
        return new ArrayList<>(searchResults);
    }
}



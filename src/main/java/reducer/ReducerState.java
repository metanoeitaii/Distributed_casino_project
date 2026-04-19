package reducer;

import java.util.HashMap;
import java.util.Map;

// koino state metajy twn threads tou reducer 
public class ReducerState {

    private Map<String, Double> totals;
    private int expectedWorkers;
    private int finishedWorkers;

    public ReducerState(int expectedWorkers) {
        this.expectedWorkers = expectedWorkers;
        this.finishedWorkers = 0;
        this.totals = new HashMap<>();
    }

    public synchronized void addPair(String key, double value) {//prosthetei value sto key h dimiourgei neo key an den yparxei
        if (totals.containsKey(key)) {
            totals.put(key, totals.get(key) + value);
        } else {
            totals.put(key, value);
        }
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
}

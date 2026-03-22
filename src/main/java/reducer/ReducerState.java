package reducer;

import java.util.HashMap;
import java.util.Map;

public class ReducerState {

    private Map<String, Double> totals;
    private int expectedWorkers;//posa workers perimenoume 
    private int finishedWorkers;//posa workers exoun teleiwsei

    public ReducerState(int expectedWorkers) {
        this.expectedWorkers = expectedWorkers;
        this.finishedWorkers = 0;
        this.totals = new HashMap<>();
    }

    public synchronized void addPair(String key, double value) {//an yparxei to key tote prosthetei to value alliws dimiourgei neo key me to value
        if (totals.containsKey(key)) {
            totals.put(key, totals.get(key) + value);
        } else {
            totals.put(key, value);
        }
    }

    public synchronized boolean workerFinished() {
        finishedWorkers++;

        if (finishedWorkers == expectedWorkers) {
            notifyAll();
            return true;
        }

        return false;
    }

    public synchronized void waitUntilAllWorkersFinish() throws InterruptedException {
        while (finishedWorkers < expectedWorkers) {
            wait();
        }
    }

    public synchronized Map<String, Double> getTotalsCopy() {
        return new HashMap<>(totals);
    }
}

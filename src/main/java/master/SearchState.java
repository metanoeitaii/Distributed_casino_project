package master;
import java.util.ArrayList;
import java.util.List;
//object pou moirazontai ta threads gia na pairnoun ta apotelesmata 
public class SearchState {
    private List<String> apotelesmata;
    private int workersperimenw;    
    private int workersteliosane;
    
    public SearchState(int workersperimenw){
        this.workersperimenw = workersperimenw;
        this.workersteliosane = 0;
        this.apotelesmata = new ArrayList<>();
    }
    public synchronized void addApotel(String apotel){
        apotelesmata.add(apotel);

    }
    public synchronized boolean workerTeliose(){
        workersteliosane++;
        if(workersteliosane==workersperimenw){
            notifyAll();
            return true;

        }
        return false;

    }
    // client handler perimenei na teliosoun oloi oi workers 
    public synchronized void perimenw_finishworkers() throws InterruptedException{
        while (workersteliosane<workersperimenw) {
            wait();
        }
    } public synchronized List<String> getApotelesmataa(){
        return new ArrayList<>(apotelesmata); //epistrefw antigrafo listas 
    }

}

package common;

public class Player{
    private String playerId;
    private double balance;

    public Player(String playerId, double initialBalance){
        this.playerId = playerId;
        this.balance = initialBalance;
    }

    // getters
    public String getPlayerId() { return playerId; } // gia na broume ton player sto hashmap
    public double getBalance() { return balance; }

    //pros8etei poso sto ypoloipo
    //kaleitai apo WorkerStorage.addBalance() otan erxetai ADD_BALANCE request apo master 
    //kaleitai apo handlePlay otan o player wins gia epistrofh kerdwn 
    public synchronized void addBalance(double amount){
        balance += amount;
    }

    //prin to bet afairei to poso, false an den yparxei arketo balance
    //kaleitai apo handlePlay prin to bet gia na dei an exei arketo balance o player kai na to afairesei
    public synchronized boolean deductBalance(double amount){
        if(balance < amount){
            return false;
        }
        balance -= amount;
        return true;
    }
}
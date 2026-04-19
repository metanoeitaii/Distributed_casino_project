package common;
import java.io.Serializable;

// enas player sto systhma 
public class Player implements Serializable{
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
    public synchronized void addBalance(double amount){
        balance += amount;
    }

    //prin to bet afairei to poso, false an den yparxei arketo balance
    public synchronized boolean deductBalance(double amount){
        if(balance < amount){
            return false;
        }
        balance -= amount;
        return true;
    }
}

//paixnidi sto systhma 
package common;
import srg.*;
import java.io.Serializable;

public class Game implements Serializable{

    private String GameName; //monadiko onoma game
    private String ProviderName; //onoma paroxou
    private double Stars; //MO ba8mologias
    private int NoOfVotes; //plh8os pshfwn (gia epanaypologismo)
    private String GameLogo; //path logotypou
    private double MinBet; //min pontarisma
    private double MaxBet; //max pontarisma
    private String RiskLevel; //epipedo riskou (low, medium, high)
    private String HashKey; //mystiko kleidi gia epalh8eush me ton SRG
    private String betCategory; //kathgoria pontarismatos ($/$$/$$$)
    private int jackpot; //timh jackpot, analoga me RiskLevel
    private boolean isActive; //false, an o manager ekane remove to game 
    private double totalProfitLoss; //total kerdh/zhmies systhmatos apo to game
    private RandomNumberBuffer randomBuffer; //buffer random ari8mwn poy trofodoteitai apo SRG

    //Constructor: game me JSON stoixeia
    public Game(String GameName , String ProviderName, double Stars, int NoOfVotes, String GameLogo, double MinBet, double MaxBet, String RiskLevel, String HashKey){
        this.GameName  = GameName ;
        this.ProviderName = ProviderName;
        this.Stars = Stars;
        this.NoOfVotes = NoOfVotes;
        this.GameLogo = GameLogo;
        this.MinBet = MinBet;
        this.MaxBet = MaxBet;
        this.RiskLevel = RiskLevel;
        this.HashKey = HashKey;
        this.isActive = true; //ka8e neo paixnidi einai active ex arxhs
        this.totalProfitLoss = 0.0; 
        this.betCategory = calculateBetCategory(); //ypologizetai aytomata apo to MinBet
        this.jackpot = calculateJackpot(); //ypologizetai aytomata apo to RiskLevel
    }

    //ypologizei thn kathgoria pontarismatos basei toy min bet 
    private String calculateBetCategory(){
        if(MinBet >= 0.1 && MinBet < 1){ 
            return "$";
        }else if(MinBet >= 1.0 && MinBet < 5){ 
            return "$$";
        }else{ 
            return "$$$";
        }
    }

    //ypologizei jackpot basei risk level
    private int calculateJackpot(){
        switch(RiskLevel.toLowerCase()){
            case "low": //low -> 10
                return 10;
            case "medium": //medium -> 20
                return 20;
            case "high": //high -> 40
                return 40;
            default:
                System.out.println("Error: Invalid RiskLevel " + RiskLevel);
                return 10; //fallback gia allh timh
        }
    }

    //getters
    public String getGameName () { return GameName ; }
    public String getProviderName() { return ProviderName; }
    public double getStars() { return Stars; }
    public int getNoOfVotes() { return NoOfVotes; }
    public double getMinBet() { return MinBet; }
    public double getMaxBet() { return MaxBet; }
    public String getRiskLevel() { return RiskLevel; }
    public String getHashKey() { return HashKey; }
    public String getBetCategory() { return betCategory; }
    public int getJackpot() { return jackpot; }
    public boolean isActive() { return isActive; }
    public double getTotalProfitLoss() { return totalProfitLoss; }
    public String getGameLogo() { return GameLogo; }

    //setters
    
    //de sbhnoume game, to kanoume apla inactive (boh8eia se manager queries)
    public void setActive(boolean active){
        isActive = active;
    }

    //allazei risk level kai ypologizei neo jackpot 
    public void setRiskLevel(String RiskLevel){
        this.RiskLevel = RiskLevel;
        this.jackpot = calculateJackpot();
    }

    //enhmerwnei total kerdh/zhmies systhmatos(game)
    //synchronized gia apotroph race condition apo taytoxrona bets
    public synchronized void addProfitLoss(double amount){
        this.totalProfitLoss += amount;
    }

    //pros8etei nea pshfo kai ypologizei neo MO Stars
    public synchronized void addVote(int newStars){
        double total = Stars * NoOfVotes;
        total = total + newStars;
        NoOfVotes++;
        Stars = total / NoOfVotes;
    }

    //jekina SRG Client gia to game
    public void initSRG(String srgHost, int srgPort){
        randomBuffer = new RandomNumberBuffer(10); //buffer me capacity 10 numbers
        SRGClient client = new SRGClient(srgHost, srgPort, HashKey, randomBuffer); //dhmioyrgei ton SRG client poy 8a synde8ei me SRG server
        Thread t = new Thread(client); //se jexwristo thread 
        t.setDaemon(true); // stamataei aytomata otan teleiwsei to kyrio programma
        t.start();
    }

    //pairnei epomeno tyxaio number apo buffer
    public int getRandomNumber() throws InterruptedException{ //an adeio buffer, perimenei mexri SRG na bgalei neo number 
        return randomBuffer.consume();
    }
}
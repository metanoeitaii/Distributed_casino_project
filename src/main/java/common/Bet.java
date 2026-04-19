package common;
import java.io.Serializable;

// memonwmeno bet enos player 
public class Bet implements Serializable {
    private String playerId; 
    private String GameName;
    private String ProviderName;
    private double betAmount; 
    private double multiplier; // syntelesths apo pinaka riskoy 
    private double result; 

    public Bet(String playerId, String GameName, String ProviderName, double betAmount, double multiplier){
        this.playerId = playerId;
        this.GameName = GameName;
        this.ProviderName = ProviderName;
        this.betAmount = betAmount;
        this.multiplier = multiplier;
        this.result = calculateResult();
    }

    // an multiplier = 0, zhmia = betAmount
    public double calculateResult(){
        double winAmount = betAmount * multiplier;
        double result = winAmount - betAmount;
        return result;
    }

    //getters
    public String getPlayerId() { return playerId; }
    public String getGameName() { return GameName; }
    public String getProviderName() { return ProviderName; }
    public double getBetAmount() { return betAmount; }
    public double getMultiplier() { return multiplier; }
    public double getResult() { return result; }
}

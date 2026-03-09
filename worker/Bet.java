public class Bet{
    private String playerId; //id player poy ekane to bet
    private String GameName;
    private String ProviderName;
    private double betAmount; //poso bet
    private double multiplier; //syntelesths poy bghke apo ton pinaka riskoy
    private double result; //teliko kerdos/zhmia (<0 an exase)

    public Bet(String playerId, String GameName, String ProviderName, double betAmount, double multiplier){
        this.playerId = playerId;
        this.GameName = GameName;
        this.ProviderName = ProviderName;
        this.betAmount = betAmount;
        this.multiplier = multiplier;
        this.result = calculateResult();
    }

    public double calculateResult(){
        double winAmount = betAmount * multiplier;
        double result = winAmount - betAmount;
        return result; // an multiplier = 0, zhmia = betAmount
    }

    //getters
    public String getPlayerId() { return playerId; }
    public String getGameName() { return GameName; }
    public String getProviderName() { return ProviderName; }
    public double getBetAmount() { return betAmount; }
    public double getMultiplier() { return multiplier; }
    public double getResult() { return result; }
}
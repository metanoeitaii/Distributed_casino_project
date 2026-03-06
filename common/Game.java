package common;

public class Game {
    public String GameName;
    public String ProviderName;
    public int Stars;
    public int NoOfVotes;
    public String GameLogo;
    public double MinBet;
    public double MaxBet;
    public String RiskLevel;
    public String HashKey;
    public String BetCategory;
    public int Jackpot;

    public void upologismos_pontarismatos_jackpot(){
        if(MinBet >= 0.1 && MinBet < 1){                     //upologismos pontarismatwn
            BetCategory = "$";
        } else if(MinBet >= 1 && MinBet < 5){
            BetCategory = "$$";
        } else {
            BetCategory = "$$$";
        }
        //Upologismos jackpot
        if(RiskLevel.equals("low")){
            Jackpot = 10;
        } else if(RiskLevel.equals("medium")){
            Jackpot = 20;
        } else if(RiskLevel.equals("high")){
            Jackpot = 40;
        }else{
            System.out.println("Error ");
        }
    }
}
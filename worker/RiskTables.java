//pinakes pollaplasiastwn gia ka8e epipedo riskou 
public class RiskTables{ 

    //an randomNumber % 100 == 0 -> jackpot, oxi pinakas 
    //alliws i = randomNumber % 10 -> multiplier apo pinaka, analoga RiskLevel 
    public static final double[] LOW = {0.0, 0.0, 0.0, 0.1, 0.5, 1.0, 1.1, 1.3, 2.0, 2.5};
    public static final double[] MEDIUM = {0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 1.0, 1.5, 2.5, 3.5};
    public static final double[] HIGH = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.5};

    //epistrofh pinaka gia to risk level game 
    public static double[] getTable(String riskLevel){
        switch(riskLevel.toLowerCase()){
            case "low":
                return LOW;
            case "medium":
                return MEDIUM;
            case "high":
                return HIGH;
            default:
                return LOW; //gia na mhn krasarei an er8ei allh timh 
        }
    }
}
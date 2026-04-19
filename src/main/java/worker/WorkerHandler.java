package worker;

import common.Message;
import java.util.List;
import java.util.ArrayList;
import java.net.*; 
import java.io.*;
import common.*;
import srg.*;

// xeirizetai ka8e request apo ton master se jexwristo thread
public class WorkerHandler implements Runnable{
    private Socket socket; 
    private WorkerStorage storage;
    private String srgHost; 
    private int srgPort;
    private String reducerHost;
    private int reducerPort;

    public WorkerHandler(Socket socket, WorkerStorage storage, String srgHost, int srgPort, String reducerHost, int reducerPort){
        this.socket = socket;
        this.storage = storage;
        this.srgHost = srgHost;
        this.srgPort = srgPort; 
        this.reducerHost = reducerHost;
        this.reducerPort = reducerPort;
    }

    @Override
    public void run(){
        try(
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        ) {
            String requestType = (String) in.readObject();

            switch(requestType){ // dromologhsh request sth swsth method 
                case Message.ADD_GAME:
                    handleAddGame(in, out);
                    break;
                case Message.REMOVE_GAME:
                    handleRemoveGame(in, out);
                    break;
                case Message.UPDATE_RISK:
                    handleUpdateRisk(in, out);
                    break;
                case Message.SEARCH:
                    handleSearch(in, out);
                    break;
                case Message.PLAY:
                    handlePlay(in, out);
                    break;
                case Message.MAP:
                    handleMap(in, out);
                    break;
                case Message.ADD_BALANCE:
                    handleAddBalance(in, out);
                    break;
                case Message.VOTE:
                    handleVote(in, out);
                    break;
                default:
                    out.writeObject(Message.ERROR + ": UNKNOWN REQUEST");
                
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void handleAddGame(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        
        String GameName = (String) in.readObject();
        String ProviderName = (String) in.readObject();

        String starsStr = (String) in.readObject();
        double Stars = Double.parseDouble(starsStr); 

        String votesStr = (String) in.readObject();
        int NoOfVotes = Integer.parseInt(votesStr);

        String GameLogo = (String) in.readObject();

        String minStr = (String) in.readObject();
        double MinBet = Double.parseDouble(minStr);

        String maxStr = (String) in.readObject();
        double MaxBet = Double.parseDouble(maxStr);

        String RiskLevel = (String) in.readObject();
        String HashKey = (String) in.readObject();

        Game game = new Game(GameName, ProviderName, Stars, NoOfVotes, GameLogo, MinBet, MaxBet, RiskLevel, HashKey);
        game.initSRG(srgHost, srgPort);
        storage.addGame(game);
        out.writeObject(Message.OK);
        out.flush();
    }

    private void handleRemoveGame(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException {
        String GameName = (String) in.readObject(); 
        Game game = storage.getGame(GameName); 

        if(game == null){ 
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        storage.removeGame(GameName); // isActive = false, de diagrafoyme dedomena 
        out.writeObject(Message.OK); 
        out.flush();
    }

    private void handleUpdateRisk(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String GameName = (String) in.readObject();
        String newRiskLevel = (String) in.readObject(); 
        Game game = storage.getGame(GameName); 

        if(game == null){
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        storage.updateRiskLevel(GameName, newRiskLevel); 
        out.writeObject(Message.OK);
        out.flush();
    }

    private void handleSearch(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException {
        String betCategory = (String) in.readObject();
        String RiskLevel = (String) in.readObject();

        String minStarsStr = (String) in.readObject();
        double minStars = Double.parseDouble(minStarsStr);

        List<Game> results = new ArrayList<>();  
        for(Game game: storage.getActiveGames()){
            if(betCategory.equalsIgnoreCase("ALL") || game.getBetCategory().equals(betCategory)){
                if(RiskLevel.equalsIgnoreCase("ALL") || game.getRiskLevel().equalsIgnoreCase(RiskLevel)){
                    if(game.getStars() >= minStars){
                        results.add(game);
                    }
                }
            }
        }

        for(Game game : results){
            out.writeObject(game.getGameName());
            out.writeObject(game.getProviderName());
            out.writeObject(game.getGameLogo());
            out.writeObject(game.getStars());
            out.writeObject(game.getNoOfVotes());
            out.writeObject(game.getMinBet());
            out.writeObject(game.getMaxBet());
            out.writeObject(game.getRiskLevel());
            out.writeObject(game.getBetCategory());
            out.writeObject(game.getJackpot());
            
        }
        out.writeObject(Message.END);
        out.flush();
    }

    private void handlePlay(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String playerId = (String) in.readObject(); 
        String GameName = (String) in.readObject(); 
        String betAmountStr = (String) in.readObject(); 
        double betAmount = Double.parseDouble(betAmountStr);

        Game game = storage.getGame(GameName);
        if(game == null || !game.isActive()){ 
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        if(betAmount < game.getMinBet() || betAmount > game.getMaxBet()){
            out.writeObject(Message.ERROR + ": BET AMOUNT IS OUTSIDE THE ALLOWED RANGE");
            out.flush();
            return;
        }

        Player player = storage.getOrCreatePlayer(playerId); 
        boolean hasBalance = player.deductBalance(betAmount); 
        if(!hasBalance){
            out.writeObject(Message.ERROR + ": NOT ENOUGH BALANCE");
            out.flush();
            return;
        }

        int randomNumber; 
        try{ 
            randomNumber = game.getRandomNumber(); 
        }catch(InterruptedException e){ 
            player.addBalance(betAmount); // epistrofh xrhmatwn se sfalma 
            out.writeObject(Message.ERROR + ": COULD NOT GET RANDOM NUMBER");
            out.flush();
            return;
        }

        double multiplier; 
        boolean isJackpot = false; 

        if(randomNumber % 100 == 0){  // jackpot 1/100 pi8anothta
            multiplier = game.getJackpot();  
            isJackpot = true; //einai jackpot
        }else{
            int index = randomNumber % 10;
            double[] table = RiskTables.getTable(game.getRiskLevel()); 
            multiplier = table[index];  
        }

        
        double winAmount = betAmount * multiplier;
        double result = winAmount - betAmount;

        if(multiplier > 0){
            player.addBalance(winAmount);
        }

        Bet bet = new Bet(playerId, GameName, game.getProviderName(), betAmount, multiplier);
        storage.addBet(bet); 

        game.addProfitLoss(-result); // to systhma kerdizei otan o player xanei (antistrofo proshmo)
        
        out.writeObject(Message.OK);
        out.flush();
        out.writeObject(result);
        out.flush();

        if(isJackpot){ 
            out.writeObject(Message.JACKPOT);
            out.flush();

        }else{ 
            out.writeObject(Message.NORMAL);
            out.flush();
        }
    }

    private void handleMap(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String mapType = (String) in.readObject(); 

        List<Bet> bets = storage.getBetHistory(); 

        // stelnei ta bets katey8eian ston reducer 
        Socket reducerSocket = new Socket (reducerHost, reducerPort);

        try{
            ObjectOutputStream reducerOut = new ObjectOutputStream(reducerSocket.getOutputStream());
    
            for(Bet bet : bets){
                if(mapType.equals("PROVIDER")){ 
                    reducerOut.writeObject(bet.getProviderName());
                    reducerOut.flush();
                    reducerOut.writeObject(-bet.getResult());
                    reducerOut.flush();
                }else if (mapType.equals("PLAYER")){ //an player
                    reducerOut.writeObject(bet.getPlayerId());
                    reducerOut.flush();
                    reducerOut.writeObject(bet.getResult()); 
                    reducerOut.flush();
                }else if (mapType.equals("GAME")){         
                    reducerOut.writeObject(bet.getGameName());
                    reducerOut.flush();
                    reducerOut.writeObject(-bet.getResult());
                    reducerOut.flush();
                }
            }
            reducerOut.writeObject(Message.END);
            reducerOut.flush();
        }finally{
            reducerSocket.close();
        }
        out.writeObject(Message.OK); 
        out.flush();
    }

    private void handleAddBalance(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String playerId = (String) in.readObject(); 
        String amountStr = (String) in.readObject();
        double amount = Double.parseDouble(amountStr);

        storage.addBalance(playerId, amount); 
        out.writeObject(Message.OK); 
        out.flush();
    }

    private void handleVote(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String GameName = (String) in.readObject(); 
        String starsStr = (String) in.readObject(); 
        int Stars = Integer.parseInt(starsStr);

        if(
            Stars < 1 || Stars > 5){ 
            out.writeObject(Message.ERROR + ": STARS MUST BE BETWEEN 1 AND 5");
            out.flush();
            return;
        }

        Game game = storage.getGame(GameName); 
        if(game == null){
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        game.addVote(Stars); 
        out.writeObject(Message.OK);
        out.flush();
    }
}

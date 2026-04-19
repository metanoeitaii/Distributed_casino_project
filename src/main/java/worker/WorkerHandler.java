package worker;

import common.Message;
import java.util.List;
import java.util.ArrayList;
import java.net.*; //gia sockets
import java.io.*;
import common.*;
import srg.*;

//xeirizetai ka8e request apo master se jexwristo thread
public class WorkerHandler implements Runnable{
    private Socket socket; // socket connection me master 
    private WorkerStorage storage; // to in-memory storage 
    private String srgHost; 
    private int srgPort;
    private String reducerHost;
    private int reducerPort;

    //pairnei to socket kai to storage apo ton Worker otan aytos dhmioyrgeitai
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

            switch(requestType){ //analoga ton typo toy request, pame se antistoixh me8odo
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
        
        //diabazei oti steilei o master 
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

        //ftiaxnei game object
        Game game = new Game(GameName, ProviderName, Stars, NoOfVotes, GameLogo, MinBet, MaxBet, RiskLevel, HashKey);
        game.initSRG(srgHost, srgPort); //jekinaei SRG client
        storage.addGame(game); //apo8hkeysh game sto hashmap
        out.writeObject(Message.OK); //eidopoihsh master oti ola kala 
        out.flush();
    }

    private void handleRemoveGame(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException {
        String GameName = (String) in.readObject(); //onoma game poy master 8elei na afairesei
        Game game = storage.getGame(GameName); //psaxnw an yparxei to game sto storage 

        if(game == null){ //an den yparxei stelnoume error
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        storage.removeGame(GameName); //isActive = false, de svhnoyme stoixeia 
        out.writeObject(Message.OK); //ola kala se master 
        out.flush();
    }

    private void handleUpdateRisk(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String GameName = (String) in.readObject();
        String newRiskLevel = (String) in.readObject(); //diabazw neo risk level
        Game game = storage.getGame(GameName); //psaxnw an yparxei to game sto storage 

        if(game == null){
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        storage.updateRiskLevel(GameName, newRiskLevel); //allazw risk level kai ypologizw neo jackpot
        out.writeObject(Message.OK); //ola kala master
        out.flush();
    }

    //psaxnei paixnidia basei filtrwn pou stelnei o player
    private void handleSearch(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException {
        String betCategory = (String) in.readObject();
        String RiskLevel = (String) in.readObject();

        String minStarsStr = (String) in.readObject();
        double minStars = Double.parseDouble(minStarsStr);

        List<Game> results = new ArrayList<>(); //lista me ta results 
        for(Game game: storage.getActiveGames()){
            if(betCategory.equalsIgnoreCase("ALL") || game.getBetCategory().equals(betCategory)){
                if(RiskLevel.equalsIgnoreCase("ALL") || game.getRiskLevel().equalsIgnoreCase(RiskLevel)){
                    if(game.getStars() >= minStars){
                        results.add(game);
                    }
                }
            }
        }

        //stelnw ta stoixeia ka8e game
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
        String playerId = (String) in.readObject(); //poios player paizei
        String GameName = (String) in.readObject(); //se poio game
        String betAmountStr = (String) in.readObject(); //posa pontarei
        double betAmount = Double.parseDouble(betAmountStr);

        Game game = storage.getGame(GameName); //elegxos an yparxei to game sto storage 
        if(game == null || !game.isActive()){ //an den yparxei h an einai inactive -> error
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        //elegxos oriwn pontarismatos 
        if(betAmount < game.getMinBet() || betAmount > game.getMaxBet()){
            out.writeObject(Message.ERROR + ": BET AMOUNT IS OUTSIDE THE ALLOWED RANGE");
            out.flush();
            return;
        }

        Player player = storage.getOrCreatePlayer(playerId); //briskei player h ton ftiaxnei an den yparxei
        boolean hasBalance = player.deductBalance(betAmount); //afairei to bet apo to balance, an den yparxei arketo balance -> false
        if(!hasBalance){ //an den exei arketo balance -> error
            out.writeObject(Message.ERROR + ": NOT ENOUGH BALANCE");
            out.flush();
            return;
        }

        int randomNumber; //random number apo buffer
        try{ // an einai adeio perimenei 
            randomNumber = game.getRandomNumber(); 
        }catch(InterruptedException e){ //an stravwsei kati -> error
            player.addBalance(betAmount); //epistrefei ta xrhmata prin fygei
            out.writeObject(Message.ERROR + ": COULD NOT GET RANDOM NUMBER");
            out.flush();
            return;
        }

        double multiplier; //syntelesths
        boolean isJackpot = false; //arxika jackpot = false;

        if(randomNumber % 100 == 0){ //an to tyxaio number diareitai akrivws me to 100 -> jackpot 
            multiplier = game.getJackpot(); // o syntelesths ginetai to jackpot toy game 
            isJackpot = true; //einai jackpot
        }else{
            int index = randomNumber % 10; //an den eina jackpot, pairnw index 0-9 apo ton random number 
            double[] table = RiskTables.getTable(game.getRiskLevel()); //pairnw ton pinaka riskou 
            multiplier = table[index]; //pairnw ton syntelesth apo th 8esh toy index toy pinaka 
        }

        //ypologizw kerdos an >0, h zhmia an <0
        double winAmount = betAmount * multiplier;
        double result = winAmount - betAmount;

        if(multiplier > 0){
            player.addBalance(winAmount);
        }


        //neo bet object gia to istoriko
        Bet bet = new Bet(playerId, GameName, game.getProviderName(), betAmount, multiplier);
        storage.addBet(bet); //apo8hkeysh bet sto betHistory gia ta mapReduce queries 

        game.addProfitLoss(-result); //enhmerwsh esodwn game, antistrofo proshmo(to systhma kerdizei otan o player xanei)
        
        out.writeObject(Message.OK);
        out.flush();
        out.writeObject(result);
        out.flush();

        if(isJackpot){ //stelnw an htan jackpot 
            out.writeObject(Message.JACKPOT);
            out.flush();

        }else{ //h kanoniko apotelesma 
            out.writeObject(Message.NORMAL);
            out.flush();
        }
    }

    private void handleMap(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String mapType = (String) in.readObject(); //ti eidous query 8elei o manager (provider / player)

        List<Bet> bets = storage.getBetHistory(); //ola ta bets poy eginan se ayton ton worker 

        //syndesh me reducer
        Socket reducerSocket = new Socket (reducerHost, reducerPort);

        try{
            ObjectOutputStream reducerOut = new ObjectOutputStream(reducerSocket.getOutputStream());
    
            for(Bet bet : bets){
                if(mapType.equals("PROVIDER")){ // an 8eloyme query ana provider 
                    reducerOut.writeObject(bet.getProviderName()); //stelnoume provider name 
                    reducerOut.flush();
                    reducerOut.writeObject(-bet.getResult()); //kai anti8eto proshmo
                    reducerOut.flush();
                }else if (mapType.equals("PLAYER")){ //an player
                    reducerOut.writeObject(bet.getPlayerId()); //playerId
                    reducerOut.flush();
                    reducerOut.writeObject(-bet.getResult()); //kai result 
                    reducerOut.flush();
                }
            }
            reducerOut.writeObject(Message.END);
            reducerOut.flush();
        }finally{
            reducerSocket.close(); //panta na kleinei
        }
    }

    private void handleAddBalance(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String playerId = (String) in.readObject(); //player poy 8elei na balei tokens
        String amountStr = (String) in.readObject(); //poso poy 8elei na pros8esei
        double amount = Double.parseDouble(amountStr);

        storage.addBalance(playerId, amount); //pros8etei to poso sto balance toy player
        out.writeObject(Message.OK); //ola kala master
        out.flush();
    }

    private void handleVote(ObjectInputStream in, ObjectOutputStream out) throws IOException , ClassNotFoundException{
        String GameName = (String) in.readObject(); //onoma game gia rate
        String starsStr = (String) in.readObject(); //rate 1-5
        int Stars = Integer.parseInt(starsStr);

        if(
            Stars < 1 || Stars > 5){ //elegxos gia oria
            out.writeObject(Message.ERROR + ": STARS MUST BE BETWEEN 1 AND 5");
            out.flush();
            return;
        }

        Game game = storage.getGame(GameName); //psaxnw an yparxei to game
        if(game == null){
            out.writeObject(Message.ERROR + ": GAME NOT FOUND");
            out.flush();
            return;
        }

        game.addVote(Stars); //pros8etei nea pshfo kai ypologizei neo MO
        out.writeObject(Message.OK);
        out.flush();
    }
}

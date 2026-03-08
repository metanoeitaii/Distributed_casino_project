import java.util.List;
import java.util.ArrayList;
import java.net.*; //gia sockets
import java.io.*;

//xeirizetai ka8e request apo master se jexwristo thread
public class WorkerHandler implements Runnable{
    private Socket socket; // socket connection me master 
    private WorkerStorage storage; // to in-memory storage 

    //pairnei to socket kai to storage apo ton Worker otan aytos dhmioyrgeitai
    public WorkerHandler(Socket socket, WorkerStorage storage){
        this.socket = socket;
        this.storage = storage;
    }

    @Override
    public void run(){
        try(
            //gia na diabazoume grammh grammh apo master 
            //diabazei grammh grammh apo to master 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //stelnei messages sto master, true -> autoflush messages
        ) {
            String requestType = in.readLine();

            switch(requestType){ //analoga ton typo toy request, pame se antistoixh me8odo
                case "ADD_GAME":
                    handleAddGame(in, out);
                    break;
                case "REMOVE_GAME":
                    handleRemoveGame(in, out);
                    break;
                case "UPDATE_RISK":
                    handleUpdateRisk(in, out);
                    break;
                case "SEARCH":
                    handleSearch(in, out);
                    break;
                case "PLAY":
                    handlePlay(in, out);
                    break;
                case "MAP":
                    handleMap(in, out);
                    break;
                case "ADD_BALANCE":
                    handleAddBalance(in, out);
                    break;
                case "VOTE":
                    handleVote(in, out);
                    break;
                default:
                    out.println("ERROR: UNKNOWN REQUEST");
                
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleAddGame(BufferedReader in, PrintWriter out) throws IOException {
        
        //diabazei oti steilei o master 
        String GameName = in.readLine();
        String ProviderName = in.readLine();

        String starsStr = in.readLine();
        double Stars = Double.parseDouble(starsStr); //

        String votesStr = in.readLine();
        int NoOfVotes = Integer.parseInt(votesStr);

        String GameLogo = in.readLine();

        String minStr = in.readLine();
        double MinBet = Double.parseDouble(minStr);

        String maxStr = in.readLine();
        double MaxBet = Double.parseDouble(maxStr);

        String RiskLevel = in.readLine();
        String HashKey = in.readLine();

        //ftiaxnei game object
        Game game = new Game(GameName, ProviderName, Stars, NoOfVotes, GameLogo, MinBet, MaxBet, RiskLevel, HashKey);
        game.initSRG("localhost", 6000); //jekinaei SRG client
        storage.addGame(game); //apo8hkeysh game sto hashmap
        out.println("OK"); //eidopoihsh master oti ola kala 
    }

    private void handleRemoveGame(BufferedReader in, PrintWriter out) throws IOException {
        String GameName = in.readLine(); //onoma game poy master 8elei na afairesei
        Game game = storage.getGame(GameName); //psaxnw an yparxei to game sto storage 

        if(game == null){ //an den yparxei stelnoume error
            out.println("ERROR: GAME NOT FOUND");
            return;
        }

        storage.removeGame(GameName); //isActive = false, de svhnoyme stoixeia 
        out.println("OK"); //ola kala se master 
    }

    private void handleUpdateRisk(BufferedReader in, PrintWriter out) throws IOException {
        String GameName = in.readLine();
        String newRiskLevel = in.readLine(); //diabazw neo risk level
        Game game = storage.getGame(GameName); //psaxnw an yparxei to game sto storage 

        if(game == null){
            out.println("ERROR: GAME NOT FOUND");
            return;
        }

        storage.updateRiskLevel(GameName, newRiskLevel); //allazw risk level kai ypologizw neo jackpot
        out.println("OK"); //ola kala master
    }

    //psaxnei paixnidia basei filtrwn pou stelnei o player
    private void handleSearch(BufferedReader in, PrintWriter out) throws IOException {
        String betCategory = in.readLine();
        String RiskLevel = in.readLine();

        String minStarsStr = in.readLine();
        double minStars = Double.parseDouble(minStarsStr);

        List<Game> results = new ArrayList<>(); //lista me ta results 
        for(Game game: storage.getActiveGames()){
            if(betCategory.equals("ALL") || game.getBetCategory().equals(betCategory)){
                if(RiskLevel.equals("ALL") || game.getRiskLevel().equalsIgnoreCase(RiskLevel)){
                    if(game.getStars() >= minStars){
                        results.add(game);
                    }
                }
            }
        }

        //stelnw ta stoixeia ka8e game
        for(Game game : results){
            out.println(game.getGameName());
            out.println(game.getProviderName());
            out.println(game.getStars());
            out.println(game.getNoOfVotes());
            out.println(game.getMinBet());
            out.println(game.getMaxBet());
            out.println(game.getRiskLevel());
            out.println(game.getBetCategory());
            out.println(game.getJackpot());
        }
        out.println("END");
    }

    private void handlePlay(BufferedReader in, PrintWriter out) throws IOException {
        String playerId = in.readLine(); //poios player paizei
        String GameName = in.readLine(); //se poio game
        String betAmountStr = in.readLine(); //posa pontarei
        double betAmount = Double.parseDouble(betAmountStr);

        Game game = storage.getGame(GameName); //elegxos an yparxei to game sto storage 
        if(game == null || !game.isActive()){ //an den yparxei h an einai inactive -> error
            out.println("ERROR: GAME NOT FOUND");
            return;
        }

        //elegxos oriwn pontarismatos 
        if(betAmount < game.getMinBet() || betAmount > game.getMaxBet()){
            out.println("ERROR: BET AMOUNT IS OUTSIDE THE ALLOWED RANGE");
            return;
        }

        Player player = storage.getOrCreatePlayer(playerId); //briskei player h ton ftiaxnei an den yparxei
        boolean hasBalance = player.deductBalance(betAmount); //afairei to bet apo to balance, an den yparxei arketo balance -> false
        if(!hasBalance){ //an den exei arketo balance -> error
            out.println("ERROR: NOT ENOUGH BALANCE");
            return;
        }

        int randomNumber; //random number apo buffer
        try{ // an einai adeio perimenei 
            randomNumber = game.getRandomNumber(); 
        }catch(InterruptedException e){ //an stravwsei kati -> error
            out.println("ERROR : COULD NOT GET RANDOM NUMBER");
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

        //an kerdise epistrefoume kerdos + arxiko bet 
        if(result > 0){
            player.addBalance(result + betAmount);
        }

        //neo bet object gia to istoriko
        Bet bet = new Bet(playerId, GameName, game.getProviderName(), betAmount, multiplier);
        storage.addBet(bet); //apo8hkeysh bet sto betHistory gia ta mapReduce queries 

        game.addProfitLoss(-result); //enhmerwsh esodwn game, antistrofo proshmo(to systhma kerdizei otan o player xanei)

        out.println("OK"); //ola kala master
        out.println(result); //kai to result ston master
        
        if(isJackpot){ //stelnw an htan jackpot 
            out.println("JACKPOT");

        }else{ //h kanoniko apotelesma 
            out.println("NORMAL");
        }
    }

    private void handleMap(BufferedReader in, PrintWriter out) throws IOException {
        String mapType = in.readLine(); //ti eidous query 8elei o manager (provider / player)

        List<Bet> bets = storage.getBetHistory(); //ola ta bets poy eginan se ayton ton worker 

        for(Bet bet : bets){
            if(mapType.equals("PROVIDER")){ // an 8eloyme query ana provider 
                out.println(bet.getProviderName()); //stelnoume provider name 
                out.println(bet.getResult()); //kai result
            }else if (mapType.equals("PLAYER")){ //an player
                out.println(bet.getPlayerId()); //playerId
                out.println(bet.getResult()); //kai result 
            }
        }
        out.println("END");
    }

    private void handleAddBalance(BufferedReader in, PrintWriter out) throws IOException{
        String playerId = in.readLine(); //player poy 8elei na balei tokens
        String amountStr = in.readLine(); //poso poy 8elei na pros8esei
        double amount = Double.parseDouble(amountStr);

        storage.addBalance(playerId, amount); //pros8etei to poso sto balance toy player
        out.println("OK"); //ola kala master
    }

    private void handleVote(BufferedReader in, PrintWriter out) throws IOException{
        String GameName = in.readLine(); //onoma game gia rate
        String starsStr = in.readLine(); //rate 1-5
        int Stars = Integer.parseInt(starsStr);

        if(
            Stars < 1 || Stars > 5){ //elegxos gia oria
            out.println("ERROR: STARS MUST BE BETWEEN 1 AND 5");
            return;
        }

        Game game = storage.getGame(GameName); //psaxnw an yparxei to game
        if(game == null){
            out.println("ERROR: GAME NOT FOUND");
            return;
        }

        game.addVote(Stars); //pros8etei nea pshfo kai ypologizei neo MO
        out.println("OK");
    }
}
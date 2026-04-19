package worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.*;
import srg.*;

//in-memory bash dedomenwn Worker, apo8hkeyei games, bets, players
public class WorkerStorage{
    private final Map<String, Game> games = new HashMap<>(); 
    private final List<Bet> betHistory = new ArrayList<>();
    private final Map<String, Player> players = new HashMap<>(); 

    public synchronized void addGame(Game game){
        games.put(game.getGameName(), game);
    }

    //de sbhnw game to kanw inactive 
    public synchronized void removeGame(String gameName){
        Game game = games.get(gameName);
        if (game != null){
            game.setActive(false);
        }
    }

    //allazei risk level kai ypologizei neo jackpot
    public synchronized void updateRiskLevel(String gameName, String newRiskLevel){
        Game game = games.get(gameName);
        if(game != null){
            game.setRiskLevel(newRiskLevel);
        }
    }

    //epistrofh game me bash to game name 
    public synchronized Game getGame(String gameName){
        return games.get(gameName);
    }

    //epistrofh active games mono 
    public synchronized List<Game> getActiveGames(){
        List<Game> active = new ArrayList<>();
        for (Game game : games.values()){
            if(game.isActive()){
                active.add(game);
            }
        }
        return active;
    }

    //epistrofh ola ta games 
    public synchronized List<Game> getAllGames(){
        return new ArrayList<>(games.values());
    }

    //apo8hkeysh bet se istoriko twn bets
    public synchronized void addBet(Bet bet){
        betHistory.add(bet);
    }

    //epistrofh antigrafo istorikou bets, gia na mhn mporei kapoios ejwterika na to tropopoihsei
    public synchronized List<Bet> getBetHistory(){
        return new ArrayList<>(betHistory);
    }

    //epistrofh paixth an yparxei, alliws dhmioyrgia paixth me balance = 0
    public synchronized Player getOrCreatePlayer(String playerId){
        Player player = players.get(playerId);
        if(player == null){
            player = new Player(playerId, 0.0);
            players.put(playerId, player);
        }
        return player;
    }

    //pros8etei balance se paixth 
    public synchronized void addBalance(String playerId, double amount){
        Player player = players.get(playerId);
        if(player == null){
            player = new Player(playerId, 0.0);
            players.put(playerId, player);
        }
        player.addBalance(amount);
    }
}

package com.example.betapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GameListFragmentActivity extends Fragment {

    private String playerId;
    private ServerConnection serverConnection = new ServerConnection("10.0.2.2", 8080);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        String stars = "0";
        String risk = "all";
        String bet = "all";

        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
            stars = getArguments().getString("stars");
            risk = getArguments().getString("risk");
            bet = getArguments().getString("bet");
        }

        RecyclerView rvGames = view.findViewById(R.id.rvGames);
        TextView tvNoGames = view.findViewById(R.id.tvNoGames);

        final String finalStars = stars;
        final String finalRisk = risk;
        final String finalBet = bet;

        serverConnection.search(finalBet, finalRisk, finalStars,new ServerConnection.Callback<List<ServerConnection.GameResult>>() {
            @Override
            public void onSuccess(List<ServerConnection.GameResult> results) {
                requireActivity().runOnUiThread(() -> {
                    List<GameItemActivity> games = new ArrayList<>();
                    for (ServerConnection.GameResult r : results) {
                        games.add(new GameItemActivity(
                                r.gameName, r.providerName,
                                Float.parseFloat(r.stars),
                                r.riskLevel, r.betCategory,
                                Float.parseFloat(r.minBet),
                                Float.parseFloat(r.maxBet),
                                Float.parseFloat(r.jackpot)));
                    }

                    if (games.isEmpty()) {
                        tvNoGames.setVisibility(View.VISIBLE);
                        rvGames.setVisibility(View.GONE);
                    } else {
                        tvNoGames.setVisibility(View.GONE);
                        rvGames.setVisibility(View.VISIBLE);

                        GameAdapterActivity adapter = new GameAdapterActivity(games, game -> {
                            Bundle args = new Bundle();
                            args.putString("playerId", playerId);
                            args.putString("gameName", game.gameName);
                            args.putString("providerName", game.providerName);
                            args.putFloat("stars", game.stars);
                            args.putString("riskLevel", game.riskLevel);
                            args.putString("betCategory", game.betCategory);
                            args.putFloat("minBet", game.minBet);
                            args.putFloat("maxBet", game.maxBet);
                            args.putFloat("jackpot", game.jackpot);
                            Navigation.findNavController(view)
                                    .navigate(R.id.gameDetailFragment, args);
                        });

                        rvGames.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvGames.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    tvNoGames.setText("Connection error: " + error);
                    tvNoGames.setVisibility(View.VISIBLE);
                    rvGames.setVisibility(View.GONE);
                });
            }
        });

        return view;
    }
}
package com.example.betapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class GameDetailFragmentActivity extends Fragment {
    //connecting with server
    private ServerConnection serverConnection = new ServerConnection("10.0.2.2", 8080);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_detail, container, false);

        TextView tvGameName = view.findViewById(R.id.tvGameName);
        TextView tvProvider = view.findViewById(R.id.tvProvider);
        TextView tvStars = view.findViewById(R.id.tvStars);
        TextView tvRisk = view.findViewById(R.id.tvRisk);
        TextView tvBet = view.findViewById(R.id.tvBet);
        TextView tvMinMax = view.findViewById(R.id.tvMinMax);
        TextView tvJackpot = view.findViewById(R.id.tvJackpot);
        TextView tvResult = view.findViewById(R.id.tvResult);
        EditText etBetAmount = view.findViewById(R.id.etBetAmount);
        Button btnPlay = view.findViewById(R.id.btnPlay);

        EditText etStars = view.findViewById(R.id.etStars);
        Button btnVote = view.findViewById(R.id.btnVote);
        TextView tvVoteResult = view.findViewById(R.id.tvVoteResult);

        String playerId = "";
        String gameName = "";
        float minBet = 0;
        float maxBet = 0;

        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
            gameName = getArguments().getString("gameName");
            float stars = getArguments().getFloat("stars");
            String riskLevel = getArguments().getString("riskLevel");
            String betCategory = getArguments().getString("betCategory");
            minBet = getArguments().getFloat("minBet");
            maxBet = getArguments().getFloat("maxBet");
            float jackpot = getArguments().getFloat("jackpot");
            String providerName = getArguments().getString("providerName");

            tvGameName.setText(gameName);
            tvProvider.setText("Provider: " + providerName);
            tvStars.setText("⭐ Stars: " + stars);
            tvRisk.setText("Risk Level: " + riskLevel);
            tvBet.setText("Bet Limit: " + betCategory);
            tvMinMax.setText("Min Bet: " + minBet + " | Max Bet: " + maxBet);
            tvJackpot.setText("🏆 Jackpot: x" + jackpot);
        }

        final String finalPlayerId = playerId;
        final String finalGameName = gameName;

        TextView tvPlayerBalance = view.findViewById(R.id.tvPlayerBalance);

        serverConnection.getBalance(finalPlayerId, new ServerConnection.Callback<String>() {
            @Override
            public void onSuccess(String balance) {
                requireActivity().runOnUiThread(() ->
                        tvPlayerBalance.setText("Your Balance: " + balance + " FUN"));
            }
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        tvPlayerBalance.setText("Your Balance: -- FUN"));
            }
        });

        final float finalMinBet = minBet;
        final float finalMaxBet = maxBet;

        btnPlay.setOnClickListener(v -> {
            String betStr = etBetAmount.getText().toString().trim();
            if (betStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter a bet amount!", Toast.LENGTH_SHORT).show();
                return;
            }

            float betAmount = Float.parseFloat(betStr);
            if (betAmount < finalMinBet || betAmount > finalMaxBet) {
                Toast.makeText(getContext(),
                        "Bet must be between " + finalMinBet + " and " + finalMaxBet,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            //connecting with server
            tvResult.setText("Sending bet to server...");
            serverConnection.play(finalGameName, finalPlayerId, String.valueOf(betAmount), new ServerConnection.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    requireActivity().runOnUiThread(() -> {
                        String[] parts = result.split("\\|");
                        String status = parts[0];
                        String winLoss = parts[1];
                        String type = parts[2];
                        double winLossDouble = Double.parseDouble(winLoss);
                        String formatted = String.format("%.2f", Math.abs(winLossDouble));
                        if (status.startsWith("ERROR")) {
                            tvResult.setText("❌ " + status);
                        } else if (type.equals("JACKPOT")) {
                            tvResult.setText("🏆 JACKPOT! You won: " + formatted + " FUN!");
                        } else if (Double.parseDouble(winLoss) > 0) {
                            tvResult.setText("🏆 You won: " + formatted + " FUN!");
                        } else {
                            tvResult.setText("😞 You lost: " + formatted + " FUN");
                        }

                        serverConnection.getBalance(finalPlayerId, new ServerConnection.Callback<String>() {
                            @Override
                            public void onSuccess(String balance) {
                                requireActivity().runOnUiThread(() ->
                                        tvPlayerBalance.setText("Your Balance: " + balance + " FUN"));
                            }
                            @Override
                            public void onError(String error) {}
                        });
                    });
                }
                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() ->
                            tvResult.setText("Error: " + error));
                }
            });
        });

        btnVote.setOnClickListener(v -> {
            String starsStr = etStars.getText().toString().trim();
            if (starsStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter stars (1-5)!", Toast.LENGTH_SHORT).show();
                return;
            }
            int stars = Integer.parseInt(starsStr);
            if (stars < 1 || stars > 5) {
                Toast.makeText(getContext(), "Stars must be between 1 and 5!", Toast.LENGTH_SHORT).show();
                return;
            }
            tvVoteResult.setText("Sending vote...");
            serverConnection.vote(finalGameName, starsStr, new ServerConnection.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    requireActivity().runOnUiThread(() ->
                            tvVoteResult.setText("✅ Vote submitted!"));
                }
                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() ->
                            tvVoteResult.setText("Error: " + error));
                }
            });
        });
        return view;
    }
}
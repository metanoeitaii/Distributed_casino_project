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

public class AddBalanceFragmentActivity extends Fragment {
    //connectig with server
    private ServerConnection serverConnection = new ServerConnection("10.0.2.2", 8080);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);

        String playerId = "";
        if (getArguments() != null) {
            playerId = getArguments().getString("playerId");
        }

        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAddBalance = view.findViewById(R.id.btnAddBalance);
        TextView tvResult = view.findViewById(R.id.tvResult);
        TextView tvCurrentBalance = view.findViewById(R.id.tvCurrentBalance);

        final String finalPlayerId = playerId;

        serverConnection.getBalance(finalPlayerId, new ServerConnection.Callback<String>() {
            @Override
            public void onSuccess(String balance) {
                requireActivity().runOnUiThread(() ->
                        tvCurrentBalance.setText("Current Balance: " + balance + " FUN"));
            }
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        tvCurrentBalance.setText("Current Balance: -- FUN"));
            }
        });

        btnAddBalance.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter an amount!", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount = Float.parseFloat(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Amount must be greater than 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            tvResult.setText("Sending request to server...");
            serverConnection.addBalance(finalPlayerId, amountStr, new ServerConnection.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    requireActivity().runOnUiThread(() -> {
                        String[] parts = result.split(",");
                        if (parts.length > 1) {
                            tvCurrentBalance.setText("Current Balance: " + parts[1] + " FUN");
                        }
                        tvResult.setText("✅ Balance of " + amountStr + " FUN added successfully!");
                    });
                }
                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() ->
                            tvResult.setText("Error: " + error));
                }
            });
        });

        return view;
    }
}
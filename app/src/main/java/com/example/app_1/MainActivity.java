package com.example.app_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText etAmount;
    Spinner spFrom, spTo;
    Button btnConvert, btnSettings;
    TextView tvResult;

    String[] currencies = {"INR", "USD", "EUR", "JPY"};
    HashMap<String, Double> rates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the saved theme BEFORE setting the layout
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.etAmount);
        spFrom = findViewById(R.id.spFrom);
        spTo = findViewById(R.id.spTo);
        btnConvert = findViewById(R.id.btnConvert);
        btnSettings = findViewById(R.id.btnSettings);
        tvResult = findViewById(R.id.tvResult);

        // Currency rates (base INR)
        rates.put("INR", 1.0);
        rates.put("USD", 0.012);
        rates.put("EUR", 0.011);
        rates.put("JPY", 1.8);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> convertCurrency());

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void convertCurrency() {
        String input = etAmount.getText().toString();

        if (input.isEmpty()) {
            tvResult.setText("Enter valid amount");
            return;
        }

        try {
            double amount = Double.parseDouble(input);
            String from = spFrom.getSelectedItem().toString();
            String to = spTo.getSelectedItem().toString();

            double result = amount * (rates.get(to) / rates.get(from));

            tvResult.setText(String.format("%.2f %s", result, to));
        } catch (NumberFormatException e) {
            tvResult.setText("Invalid format");
        }
    }
}
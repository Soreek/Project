package com.example.firstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private EditText deckNameEditText;
    private Button addDeckButton;
    private ListView decksListView;

    private ArrayList<String> decksList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        deckNameEditText = findViewById(R.id.deckNameEditText);
        addDeckButton = findViewById(R.id.addDeckButton);
        decksListView = findViewById(R.id.decksListView);

        sharedPreferences = getSharedPreferences("AnkiPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Пользователь");
        welcomeTextView.setText("Аккаунт: " + username);

        // Загрузка сохраненных колод
        Set<String> savedDecks = sharedPreferences.getStringSet("decks", new HashSet<>());
        decksList = new ArrayList<>(savedDecks);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, decksList);
        decksListView.setAdapter(adapter);

        addDeckButton.setOnClickListener(v -> {
            String deckName = deckNameEditText.getText().toString().trim();
            if (!deckName.isEmpty() && !decksList.contains(deckName)) {
                decksList.add(deckName);
                saveDecks();
                deckNameEditText.setText("");
                adapter.notifyDataSetChanged();
            } else {
                // ИСПРАВЛЕНО: Toast.makeText вместо Toast.java
                Toast.makeText(this, "Колода пуста или уже существует", Toast.LENGTH_SHORT).show();
            }
        });

        decksListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDeck = decksList.get(position);
            Intent intent = new Intent(MainActivity.this, activity_cards.class);
            intent.putExtra("deck_name", selectedDeck);
            startActivity(intent);
        });
    }

    private void saveDecks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("decks", new HashSet<>(decksList));
        editor.apply();
    }
}
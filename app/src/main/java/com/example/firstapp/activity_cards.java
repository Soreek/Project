package com.example.firstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class activity_cards extends AppCompatActivity {

    private TextView cardCounterTextView, cardTextTextView;
    private ImageView cardImageView;
    private Button knewButton, didNotKnowButton, addCardScreenButton;

    private String deckName;
    private ArrayList<Card> cardsList;
    private int currentIndex = 0;
    private SharedPreferences sharedPreferences;

    // Модель карточки
    public static class Card {
        String text;
        String imageUri;

        public Card(String text, String imageUri) {
            this.text = text;
            this.imageUri = imageUri;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        deckName = getIntent().getStringExtra("deck_name");

        cardCounterTextView = findViewById(R.id.cardCounterTextView);
        cardTextTextView = findViewById(R.id.cardTextTextView);
        cardImageView = findViewById(R.id.cardImageView);
        knewButton = findViewById(R.id.knewButton);
        didNotKnowButton = findViewById(R.id.didNotKnowButton);
        addCardScreenButton = findViewById(R.id.addCardScreenButton);

        sharedPreferences = getSharedPreferences("AnkiPrefs", MODE_PRIVATE);
        loadCards();

        updateCardView();

        addCardScreenButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_cards.this, AddCardActivity.class);
            intent.putExtra("deck_name", deckName);
            startActivity(intent);
        });

        // Логика кнопки "Знал"
        knewButton.setOnClickListener(v -> {
            moveToNextCard("Вы ответили: Знал");
        });

        // Логика кнопки "Не знал"
        didNotKnowButton.setOnClickListener(v -> {
            moveToNextCard("Вы ответили: Не знал");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCards();
        updateCardView();
    }

    private void loadCards() {
        String json = sharedPreferences.getString("cards_" + deckName, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Card>>() {}.getType();
        cardsList = gson.fromJson(json, type);
        if (cardsList == null) {
            cardsList = new ArrayList<>();
        }
    }

    private void updateCardView() {
        if (cardsList.isEmpty()) {
            cardCounterTextView.setText("Колода пуста");
            cardTextTextView.setText("Добавьте карточки для изучения");
            cardImageView.setVisibility(View.GONE);
            knewButton.setEnabled(false);
            didNotKnowButton.setEnabled(false);
            return;
        }

        knewButton.setEnabled(true);
        didNotKnowButton.setEnabled(true);

        if (currentIndex >= cardsList.size()) {
            currentIndex = 0; // Цикл повторения заново
            Toast.makeText(this, "Круг повторения завершен!", Toast.LENGTH_SHORT).show();
        }

        Card card = cardsList.get(currentIndex);
        cardCounterTextView.setText("Карточка " + (currentIndex + 1) + " из " + cardsList.size());
        cardTextTextView.setText(card.text);

        if (card.imageUri != null && !card.imageUri.isEmpty()) {
            cardImageView.setVisibility(View.VISIBLE);
            cardImageView.setImageURI(Uri.parse(card.imageUri));
        } else {
            cardImageView.setVisibility(View.GONE);
        }
    }

    private void moveToNextCard(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        currentIndex++;
        updateCardView();
    }
}
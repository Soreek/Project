package com.example.firstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddCardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText cardTextInput;
    private ImageView selectedImageView;
    private Button selectImageButton, saveCardButton;

    private String deckName;
    private String imageUriString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        deckName = getIntent().getStringExtra("deck_name");

        cardTextInput = findViewById(R.id.cardTextInput);
        selectedImageView = findViewById(R.id.selectedImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveCardButton = findViewById(R.id.saveCardButton);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        saveCardButton.setOnClickListener(v -> {
            String text = cardTextInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Введите текст карточки", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCardToDeck(new activity_cards.Card(text, imageUriString));
            Toast.makeText(this, "Карточка добавлена!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUriString = uri.toString();
                selectedImageView.setVisibility(View.VISIBLE);
                selectedImageView.setImageURI(uri);
            }
        }
    }

    private void saveCardToDeck(activity_cards.Card newCard) {
        SharedPreferences sharedPreferences = getSharedPreferences("AnkiPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("cards_" + deckName, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<activity_cards.Card>>() {}.getType();

        // Удалили строчку с CardListHelper, оставляем только правильный список:
        ArrayList<activity_cards.Card> list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(newCard);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cards_" + deckName, gson.toJson(list));
        editor.apply();
    }
}
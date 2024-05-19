package com.example.MAU.Notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.MAU.R;

public class NoteDetailsActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        backButton = findViewById(R.id.backButton);

        // Получение информации о заметке из Intent
        Intent intent = getIntent();
        String description = intent.getStringExtra("note_description");
        String date = intent.getStringExtra("note_date");

        // Установка инфы из карточки
        TextView descriptionTextView = findViewById(R.id.textViewDescription);
        TextView dateTextView = findViewById(R.id.textViewDate);
        descriptionTextView.setText(description);
        dateTextView.setText(date);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

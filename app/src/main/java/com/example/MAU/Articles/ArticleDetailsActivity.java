package com.example.MAU.Articles;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.example.MAU.R;

public class ArticleDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        // Получение данных о статье из Intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String articleText = getIntent().getStringExtra("articleText");

        // Настройка TextView для отображения данных о статье
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView articleTextView = findViewById(R.id.articleTextTextView);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        articleTextView.setText(HtmlCompat.fromHtml(articleText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

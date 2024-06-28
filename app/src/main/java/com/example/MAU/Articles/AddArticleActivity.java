package com.example.MAU.Articles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Articles;
import com.google.firebase.auth.FirebaseAuth;

public class AddArticleActivity extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText, photoURLEditText, articleTextEditText;
    private Button addArticleButton;
    private ImageButton backButton, boldButton, italicButton, paragraph, h1Button, h2Button, h3Button, ulButton, liButton;
    private ArticlesManager articlesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_add_article);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        initializeComponents();
        setButtonListeners();
    }

    private void initializeComponents() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        articleTextEditText = findViewById(R.id.articleTextEditText);
        photoURLEditText = findViewById(R.id.photoURLEditText);
        addArticleButton = findViewById(R.id.addArticleButton);
        backButton = findViewById(R.id.backButton);

        boldButton = findViewById(R.id.boldButton);
        italicButton = findViewById(R.id.italicButton);
        paragraph = findViewById(R.id.paragraph);
        h1Button = findViewById(R.id.h1Button);
        h2Button = findViewById(R.id.h2Button);
        h3Button = findViewById(R.id.h3Button);
        ulButton = findViewById(R.id.ulButton);
        liButton = findViewById(R.id.liButton);

        articlesManager = new ArticlesManager();
    }

    private void setButtonListeners() {
        View.OnClickListener formatButtonClickListener = v -> formatText(v);
        boldButton.setOnClickListener(formatButtonClickListener);
        italicButton.setOnClickListener(formatButtonClickListener);
        paragraph.setOnClickListener(formatButtonClickListener);
        h1Button.setOnClickListener(formatButtonClickListener);
        h2Button.setOnClickListener(formatButtonClickListener);
        h3Button.setOnClickListener(formatButtonClickListener);
        ulButton.setOnClickListener(formatButtonClickListener);
        liButton.setOnClickListener(formatButtonClickListener);

        addArticleButton.setOnClickListener(this::addArticle);
        backButton.setOnClickListener(v -> navigateBack());
    }

    private void formatText(View v) {
        String tag = (String) v.getTag();
        if (tag != null) {
            int start = articleTextEditText.getSelectionStart();
            int end = articleTextEditText.getSelectionEnd();
            if (start == end) {
                Toast.makeText(this, "Выделите текст", Toast.LENGTH_SHORT).show();
            } else {
                appendFormattedText(String.format("<%s>%s</%s>", tag, articleTextEditText.getText().toString().substring(start, end), tag), start, end);
            }
        }
    }

    private void addArticle(View v) {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String articleText = articleTextEditText.getText().toString().trim();
        String photo_URL = photoURLEditText.getText().toString().trim();
        photo_URL = photo_URL.isEmpty() ? "https://example.com/default_image.jpg" : photo_URL;

        if (!title.isEmpty() && !description.isEmpty() && !articleText.isEmpty()) {
            Articles article = new Articles(title, description, articleText, photo_URL);
            articlesManager.addArticle(article);
            navigateBack();
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "articles");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void appendFormattedText(String formattedText, int start, int end) {
        Editable editable = articleTextEditText.getText();
        editable.replace(start, end, formattedText);
        articleTextEditText.setSelection(start + formattedText.length());
    }
}

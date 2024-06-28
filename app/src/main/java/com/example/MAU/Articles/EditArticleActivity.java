package com.example.MAU.Articles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Articles;

public class EditArticleActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, articleTextEditText, photoURLEditText;
    private Button editArticleButton;
    private ImageButton backButton;
    private Articles currentArticle;
    private ArticlesManager articleManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        initializeViews();
        setupListeners();
        fetchArticleData();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        articleTextEditText = findViewById(R.id.articleTextEditText);
        photoURLEditText = findViewById(R.id.photoURLEditText);
        editArticleButton = findViewById(R.id.editArticleButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> navigateBackToMain());
        editArticleButton.setOnClickListener(this::updateArticle);
    }

    private void fetchArticleData() {
        articleManager = new ArticlesManager();
        String articleId = getIntent().getStringExtra("article_id");
        articleManager.getCurrentArticle(articleId, new ArticlesManager.OnArticleRetrievedListener() {
            @Override
            public void onArticleRetrieved(Articles article) {
                currentArticle = article;
                fillArticleData();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("EditArticleActivity", "Ошибка получения статьи: " + errorMessage);
                Toast.makeText(EditArticleActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillArticleData() {
        titleEditText.setText(currentArticle.getTitle());
        descriptionEditText.setText(currentArticle.getDescription());
        articleTextEditText.setText(currentArticle.getArticleText());
        photoURLEditText.setText(currentArticle.getPhoto_URL());
    }

    private void updateArticle(View v) {
        String editedTitle = titleEditText.getText().toString().trim();
        String editedDescription = descriptionEditText.getText().toString().trim();
        String editedArticleText = articleTextEditText.getText().toString().trim();
        String editedPhotoURL = photoURLEditText.getText().toString().trim();

        if (fieldsAreValid(editedTitle, editedDescription, editedArticleText)) {
            currentArticle.setTitle(editedTitle);
            currentArticle.setDescription(editedDescription);
            currentArticle.setArticleText(editedArticleText);
            currentArticle.setPhoto_URL(editedPhotoURL.isEmpty() ? getDefaultPhotoURL() : editedPhotoURL);

            articleManager.updateArticle(currentArticle);
            navigateBackToMain();
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fieldsAreValid(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return false;
        }
        return true;
    }

    private String getDefaultPhotoURL() {
        return "https://example.com/default_image.jpg";
    }

    private void navigateBackToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

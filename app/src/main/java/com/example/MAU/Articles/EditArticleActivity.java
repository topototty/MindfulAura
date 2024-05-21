package com.example.MAU.Articles;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Articles;

public class EditArticleActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, articleTextEditText, photoURLEditText;
    private Button editArticleButton;
    private ImageButton backButton, boldButton, italicButton, paragraph, h1Button, h2Button, h3Button;
    private Articles currentArticle;
    private ArticlesManager articleManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        // Инициализация полей EditText и кнопки
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        articleTextEditText = findViewById(R.id.articleTextEditText);
        photoURLEditText = findViewById(R.id.photoURLEditText);
        editArticleButton = findViewById(R.id.editArticleButton);
        backButton = findViewById(R.id.backButton);

        boldButton = findViewById(R.id.boldButton);
        italicButton = findViewById(R.id.italicButton);
        paragraph = findViewById(R.id.paragraph);
        h1Button = findViewById(R.id.h1Button);
        h2Button = findViewById(R.id.h2Button);
        h3Button = findViewById(R.id.h3Button);

        View.OnClickListener formatButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (tag != null) {
                    int start = articleTextEditText.getSelectionStart();
                    int end = articleTextEditText.getSelectionEnd();

                    if (start == end){
                        Toast.makeText(EditArticleActivity.this, "Выделите текст", Toast.LENGTH_SHORT).show();
                    } else {
                        String selectedText = articleTextEditText.getText().toString().substring(start, end);
                        String htmlText = String.format("<%s>%s</%s>", tag, selectedText, tag);
                        appendFormattedText(htmlText);
                    }

                }
            }
        };

        boldButton.setOnClickListener(formatButtonClickListener);
        italicButton.setOnClickListener(formatButtonClickListener);
        paragraph.setOnClickListener(formatButtonClickListener);
        h1Button.setOnClickListener(formatButtonClickListener);
        h2Button.setOnClickListener(formatButtonClickListener);
        h3Button.setOnClickListener(formatButtonClickListener);

        articleManager = new ArticlesManager();
        String articleId = getIntent().getStringExtra("article_id");

        articleManager.getCurrentArticle(articleId, new ArticlesManager.OnArticleRetrievedListener() {
            @Override
            public void onArticleRetrieved(Articles article) {
                currentArticle = article;
                titleEditText.setText(currentArticle.getTitle());
                descriptionEditText.setText(currentArticle.getDescription());
                articleTextEditText.setText(currentArticle.getArticleText());
                photoURLEditText.setText(currentArticle.getPhoto_URL());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("EditArticleActivity", "Ошибка получения текущей статьи " + errorMessage);
                Toast.makeText(EditArticleActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        editArticleButton.setOnClickListener(v -> {
            String editedTitle = titleEditText.getText().toString().trim();
            String editedDescription = descriptionEditText.getText().toString().trim();
            String editedArticleText = articleTextEditText.getText().toString().trim();
            String editedPhotoURL = photoURLEditText.getText().toString().trim();

            if (!editedTitle.isEmpty() && !editedDescription.isEmpty() && !editedArticleText.isEmpty()){
                if (currentArticle != null) {
                    if (editedPhotoURL.isEmpty()){
                        editedPhotoURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9PJscu_d_qoxDIWQnCEU-KF-6WpzI0hbMS-TmuUeaWw&s";
                    }

                    currentArticle.setTitle(editedTitle);
                    currentArticle.setDescription(editedDescription);
                    currentArticle.setArticleText(editedArticleText);
                    currentArticle.setPhoto_URL(editedPhotoURL);

                    articleManager.updateArticle(currentArticle);
                }
            } else {
                Toast.makeText(EditArticleActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            finish();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
    private void appendFormattedText(String formattedText) {
        Editable editable = articleTextEditText.getText();
        int start = articleTextEditText.getSelectionStart();
        int end = articleTextEditText.getSelectionEnd();
        editable.replace(start, end, formattedText);
        articleTextEditText.setSelection(start + formattedText.length());
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "articles");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

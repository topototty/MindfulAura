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
    private ImageButton backButton, boldButton, italicButton, paragraph, h1Button, h2Button, h3Button;
    private ArticlesManager articlesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_add_article);

        FirebaseAuth auth = FirebaseAuth.getInstance();

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

        articlesManager = new ArticlesManager();

        View.OnClickListener formatButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (tag != null) {
                    int start = articleTextEditText.getSelectionStart();
                    int end = articleTextEditText.getSelectionEnd();
                    String selectedText = articleTextEditText.getText().toString().substring(start, end);
                    String htmlText = String.format("<%s>%s</%s>", tag, selectedText, tag);
                    appendFormattedText(htmlText);
                }
            }
        };

        boldButton.setOnClickListener(formatButtonClickListener);
        italicButton.setOnClickListener(formatButtonClickListener);
        paragraph.setOnClickListener(formatButtonClickListener);
        h1Button.setOnClickListener(formatButtonClickListener);
        h2Button.setOnClickListener(formatButtonClickListener);
        h3Button.setOnClickListener(formatButtonClickListener);

        addArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String articleText = articleTextEditText.getText().toString().trim();
                String photo_URL = photoURLEditText.getText().toString().trim();

                if (!title.isEmpty() && !description.isEmpty() && !articleText.isEmpty()) {
                    if (photo_URL.isEmpty()){
                        photo_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9PJscu_d_qoxDIWQnCEU-KF-6WpzI0hbMS-TmuUeaWw&s";
                    }
                    Articles article = new Articles(title, description, articleText, photo_URL);
                    articlesManager.addArticle(article);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddArticleActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "articles");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

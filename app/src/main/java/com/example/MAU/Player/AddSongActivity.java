package com.example.MAU.Player;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddSongActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 1;
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("audio/mpeg", "audio/aac", "audio/wav", "audio/ogg");

    private Button buttonChooseFile, addSongButton;
    private ImageButton backButton;
    private EditText titleEditText, descriptionEditText, photoUrlEditText;
    private ProgressBar uploadProgressBar;
    private Uri audioUri;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        addSongButton = findViewById(R.id.addSongButton);
        backButton = findViewById(R.id.backButton);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        photoUrlEditText = findViewById(R.id.photoUrlEditText);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);

        buttonChooseFile.setOnClickListener(v -> openFileChooser());
        addSongButton.setOnClickListener(v -> uploadFile());

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            if (!isAllowedFileType(audioUri)) {
                Toast.makeText(this, "Выбранный файл имеет недопустимый формат", Toast.LENGTH_SHORT).show();
                audioUri = null;  // Reset the URI if the file format is not allowed
            } else {
                String fileName = getFileName(audioUri);
                buttonChooseFile.setText(fileName);
            }
        }
    }

    private boolean isAllowedFileType(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        return ALLOWED_MIME_TYPES.contains(mimeType);
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

    private void uploadFile() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String imageUrl = photoUrlEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (audioUri == null) {
            Toast.makeText(this, "Пожалуйста, выберите файл", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.length() > 30){
            titleEditText.setError("Название мелодии должно быть не более 30 символов");
            return;
        }
        if (description.length() > 70){
            descriptionEditText.setError("Описание мелодии должно быть не более 70 символов");
            return;
        }

        checkIfTitleExists(title, () -> {
            uploadProgressBar.setVisibility(ProgressBar.VISIBLE);
            String fileName = UUID.randomUUID().toString();
            StorageReference storageReference = storage.getReference().child("songs/" + fileName);

            storageReference.putFile(audioUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadProgressBar.setVisibility(ProgressBar.GONE);
                        String songUrl = uri.toString();
                        saveSongToFirestore(title, description, imageUrl, songUrl);
                    }))
                    .addOnFailureListener(e -> {
                        uploadProgressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(AddSongActivity.this, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void checkIfTitleExists(String title, Runnable onSuccess) {
        Query titleQuery = firestore.collection("songs")
                .whereEqualTo("title", title)
                .limit(1);

        titleQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(AddSongActivity.this, "Мелодия с таким названием уже существует", Toast.LENGTH_SHORT).show();
            } else {
                onSuccess.run();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AddSongActivity.this, "Ошибка проверки существования названия: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void saveSongToFirestore(String title, String description, String imageUrl, String songUrl) {
        if (imageUrl.isEmpty()) {
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9PJscu_d_qoxDIWQnCEU-KF-6WpzI0hbMS-TmuUeaWw&s";
        }

        Song song = new Song(title, description, imageUrl, songUrl);

        firestore.collection("songs").add(song)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddSongActivity.this, "Мелодия успешно добавлена", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddSongActivity.this, "Ошибка добавления мелодии", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AddSongActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "songs");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

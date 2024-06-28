package com.example.MAU.Notes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Note;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";

    private EditText dateEditText;
    private EditText descriptionEditText;
    private Button addNoteButton;
    private ImageButton backButton;
    private NoteManager noteManager;
    private Calendar calendar;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initViews();
        setupDateListener();
        setupListeners();
    }

    private void initViews() {
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addNoteButton = findViewById(R.id.addNoteButton);
        backButton = findViewById(R.id.backButton);
        noteManager = new NoteManager();
        calendar = Calendar.getInstance();
    }

    private void setupDateListener() {
        dateSetListener = (view, year, month, dayOfMonth) -> {
            Log.i(TAG, "Дата установлена: " + dayOfMonth + "/" + (month + 1) + "/" + year);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateEditText();
        };
        dateEditText.setOnClickListener(v -> {
            Log.d(TAG, "Нажатие на поле даты");
            new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupListeners() {
        addNoteButton.setOnClickListener(v -> addNote());
        backButton.setOnClickListener(v -> navigateBackToMain());
    }

    private void addNote() {
        Log.d(TAG, "Нажатие на кнопку добавления заметки");
        String description = descriptionEditText.getText().toString();
        if (description.isEmpty()) {
            Log.e(TAG, "Описание пустое");
            descriptionEditText.setError("Заполните это поле");
        } else {
            Note note = new Note(calendar.getTime(), description, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.d(TAG, "Создание новой заметки: " + note);
            noteManager.addNote(note);
            navigateBackToMain();
        }
    }

    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateEditText.setText(sdf.format(calendar.getTime()));
        Log.d(TAG, "Обновление текста поля даты: " + dateEditText.getText().toString());
    }

    private void navigateBackToMain() {
        Log.d(TAG, "Переход на главную активность");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBackToMain();
    }
}

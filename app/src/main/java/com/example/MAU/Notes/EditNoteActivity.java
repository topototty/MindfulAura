package com.example.MAU.Notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.MAU.MainActivity;
import com.example.MAU.R;
import com.example.MAU.models.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditNoteActivity extends AppCompatActivity {

    private NoteManager noteManager;
    private EditText editTextDescription, editTextDate;
    private Button buttonUpdate;
    private ImageButton backButton;
    private Note currentNote;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Инициализация менеджера заметок и элементов интерфейса
        noteManager = new NoteManager();
        editTextDescription = findViewById(R.id.descriptionEditText);
        editTextDate = findViewById(R.id.dateEditText);
        backButton = findViewById(R.id.backButton);
        buttonUpdate = findViewById(R.id.editNoteButton);

        // Инициализация календаря и обработчика выбора даты
        calendar = Calendar.getInstance();
        setupDateDialog();

        // Получение переданных данных о заметке для редактирования
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note")) {
            currentNote = (Note) intent.getSerializableExtra("note");
            if (currentNote != null) {
                editTextDescription.setText(currentNote.getDescription());
                editTextDate.setText(formatDate(currentNote.getDate()));
            }
        }

        // Обработчик кнопки обновления заметки
        buttonUpdate.setOnClickListener(v -> updateNote());

        // Обработчик кнопки возврата к главной активности
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
    }

    // Метод для настройки выбора даты
    private void setupDateDialog() {
        dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateEditText();
        };

        editTextDate.setOnClickListener(v -> showDatePickerDialog());
    }

    // Метод для отображения диалога выбора даты
    private void showDatePickerDialog() {
        new DatePickerDialog(
                EditNoteActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // Метод для обновления текста в поле даты
    private void updateDateEditText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    // Метод для обновления заметки
    private void updateNote() {
        if (currentNote == null) {
            return;
        }

        String updatedDescription = editTextDescription.getText().toString().trim();
        if (updatedDescription.isEmpty()) {
            editTextDescription.setError("Заполните это поле");
            return;
        }

        currentNote.setDescription(updatedDescription);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(editTextDate.getText().toString());
            currentNote.setDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        noteManager.updateNote(currentNote);
        finish();
    }

    // Метод для форматирования даты в строку
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}

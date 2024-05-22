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
    private Note currentNote;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        noteManager = new NoteManager();
        editTextDescription = findViewById(R.id.descriptionEditText);
        editTextDate = findViewById(R.id.dateEditText);
        backButton = findViewById(R.id.backButton);

        buttonUpdate = findViewById(R.id.editNoteButton);

        calendar = Calendar.getInstance();

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditNoteActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateEditText();
            }
        };

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note")) {
            currentNote = (Note) intent.getSerializableExtra("note");

            if (currentNote != null) {
                editTextDescription.setText(currentNote.getDescription());

                String formattedDate = intent.getStringExtra("formatted_date");
                editTextDate.setText(formattedDate);
            }
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNote != null) {
                    String updatedDescription = editTextDescription.getText().toString();
                    currentNote.setDescription(updatedDescription);

                    if (updatedDescription.isEmpty()) {
                        editTextDescription.setError("Заполните это поле");
                        return;
                    }

                    String updatedData = editTextDate.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                    try {
                        Date date = sdf.parse(updatedData);
                        currentNote.setDate(date);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    noteManager.updateNote(currentNote);
                    finish();
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

    private void updateDateEditText() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

}

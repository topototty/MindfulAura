package com.example.MAU.Notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.R;
import com.example.MAU.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private NoteManager noteManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewNotes;
    private NoteAdapter noteAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        noteManager = new NoteManager();

        // Настройка RecyclerView
        recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Создание и установка адаптера
        noteAdapter = new NoteAdapter(getContext(), new ArrayList<>());
        recyclerViewNotes.setAdapter(noteAdapter);
        progressBar = view.findViewById(R.id.progressBar);

        // Получение текущего пользователя
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            TextView userEmail = view.findViewById(R.id.userEmail);
            TextView userLogin = view.findViewById(R.id.userLogin);

            userEmail.setText(user.getEmail());
            userLogin.setText(user.getDisplayName());

            loadNotes(user.getUid());
        }

        ImageButton addNoteButton = view.findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNoteActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    // Создание контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu_update_delete, menu);
    }

    // Обработка нажатий на элементы контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = noteAdapter.getSelectedPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (item.getItemId() == R.id.action_edit) {
                Note currentNote = noteAdapter.getNotes().get(position);
                Intent intent = new Intent(getContext(), EditNoteActivity.class);
                intent.putExtra("note", currentNote);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = sdf.format(currentNote.getDate());
                intent.putExtra("formatted_date", formattedDate);

                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                Note currentNoteDelete = noteAdapter.getNotes().get(position);
                noteManager.deleteNote(currentNoteDelete.getNote_id());

                loadNotes(firebaseAuth.getCurrentUser().getUid());
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    // Метод для подгрузки заметок каждый раз когда открывается фрагмент
    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            loadNotes(user.getUid());
        }
    }

    // Метод для подгрузки заметок
    private void loadNotes(String userId) {
        db.collection("notes")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        List<Note> notesList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Note note = document.toObject(Note.class);
                            if (note != null) {
                                note.setNote_id(document.getId());
                                notesList.add(note);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        noteAdapter.setNotes(notesList);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Ошибка загрузки заметок", Toast.LENGTH_SHORT).show();
                        Log.d("ProfileFragment", "Error getting notes: ", task.getException());
                    }
                });
    }


}

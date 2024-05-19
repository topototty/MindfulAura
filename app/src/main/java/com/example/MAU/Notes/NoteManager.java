package com.example.MAU.Notes;

import android.util.Log;

import com.example.MAU.models.Note;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    private final String COLLECTION_NAME = "notes";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference notesCollection = db.collection(COLLECTION_NAME);

    public void getNotesByUserID(String user_id, final OnDataRetrievedListener listener) {
        List<Note> notesList = new ArrayList<>();
        notesCollection.whereEqualTo("user_id", user_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Note note = snapshot.toObject(Note.class);
                        note.setNote_id(snapshot.getId());
                        notesList.add(note);
                    }
                    listener.onNotesRetrieved(notesList);
                })
                .addOnFailureListener(e -> {
                    Log.e("NoteManager", "Error getting documents: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }

    public void addNote(Note note) {
        notesCollection.document()
                .set(note)
                .addOnSuccessListener(aVoid -> Log.d("NoteManager", "Note added successfully"))
                .addOnFailureListener(e -> Log.e("NoteManager", "Error adding note: " + e.getMessage()));
    }

    public void updateNote(Note note) {
        notesCollection.document(note.getNote_id())
                .set(note)
                .addOnSuccessListener(aVoid -> Log.d("NoteManager", "Note updated successfully"))
                .addOnFailureListener(e -> Log.e("NoteManager", "Error updating note: " + e.getMessage()));
    }

    public void deleteNote(String noteId) {
        notesCollection.document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("NoteManager", "Note deleted successfully"))
                .addOnFailureListener(e -> Log.e("NoteManager", "Error deleting note: " + e.getMessage()));
    }

    public interface OnDataRetrievedListener {
        void onNotesRetrieved(List<Note> notes);
        void onError(String errorMessage);
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }
}
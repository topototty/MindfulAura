package com.example.MAU.Player;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MAU.Articles.ArticleAdapter;
import com.example.MAU.MainActivity;
import com.example.MAU.Notes.AddNoteActivity;
import com.example.MAU.R;
import com.example.MAU.models.Articles;
import com.example.MAU.models.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView recyclerViewSongs;
    private SongAdapter songAdapter;
    private ProgressBar progressBar;

    ImageButton addSongButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        addSongButton = view.findViewById(R.id.addSongButton);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerViewSongs = view.findViewById(R.id.recyclerViewSongs);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongAdapter(getContext(), new ArrayList<>());
        recyclerViewSongs.setAdapter(songAdapter);

        loadSongs();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            TextView loginTextView = view.findViewById(R.id.loginTextView);
            loginTextView.setText(user.getDisplayName());
        }

        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddSongActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadSongs() {
        db.collection("songs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Song> songList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            progressBar.setVisibility(View.VISIBLE);
                            Song song = document.toObject(Song.class);
                            if (song != null) {
                                song.setSong_id(document.getId());
                                songList.add(song);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        songAdapter.setSongs(songList);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Ошибка загрузки треков", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
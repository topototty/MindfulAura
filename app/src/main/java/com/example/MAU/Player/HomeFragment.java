package com.example.MAU.Player;// HomeFragment.java
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.R;
import com.example.MAU.models.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView recyclerViewSongs;
    private SongAdapter songAdapter;
    private ProgressBar progressBar;
    private ImageButton addSongButton;

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

        songAdapter.setOnItemLongClickListener(this::showPopupMenu);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            TextView loginTextView = view.findViewById(R.id.loginTextView);
            loginTextView.setText(user.getDisplayName());
        }

        if (user.getDisplayName().equals("Administrator")){
            addSongButton.setVisibility(View.VISIBLE);
            addSongButton.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), AddSongActivity.class));
                getActivity().finish();
            });
        } else {
            addSongButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.context_menu_delete);
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteSong(position);
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void deleteSong(int position) {
        Song song = songAdapter.getSongs().get(position);

        StorageReference songRef = storage.getReferenceFromUrl(song.getSong_url());
        songRef.delete().addOnSuccessListener(aVoid -> {
            db.collection("songs").document(song.getSong_id())
                    .delete()
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(getContext(), "Песня удалена", Toast.LENGTH_SHORT).show();
                        songAdapter.getSongs().remove(position);
                        songAdapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления трека из базы данных", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка удаления трека из хранилища", Toast.LENGTH_SHORT).show());
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

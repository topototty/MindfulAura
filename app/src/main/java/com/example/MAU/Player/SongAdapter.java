package com.example.MAU.Player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.R;
import com.example.MAU.models.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnItemLongClickListener onItemLongClickListener;

    // Interface for long-click events
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public SongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songList.get(position);

        holder.titleTextView.setText(currentSong.getTitle());
        holder.descriptionTextView.setText(currentSong.getDescription());

        Picasso.get().load(currentSong.getImage_url()).into(holder.imageViewPhoto);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("title", currentSong.getTitle());
            intent.putExtra("description", currentSong.getDescription());
            intent.putExtra("image_url", currentSong.getImage_url());
            intent.putExtra("songUrl", currentSong.getSong_url());
            context.startActivity(intent);
        });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(Objects.equals(currentUser.getDisplayName(), "Administrator")){
            holder.itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(holder.itemView, position);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void setSongs(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    public List<Song> getSongs() {
        return songList;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView imageViewPhoto;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}

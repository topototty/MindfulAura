package com.example.MAU.Player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MAU.R;
import com.example.MAU.models.Articles;
import com.example.MAU.models.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songList;

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

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void setSongs(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

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

package com.example.MAU.Player;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MAU.R;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView progressTextView, songTitle, songDescription, finishNowLink;
    private ProgressBar circleProgressBar;
    private ImageButton playPauseButton;
    private ImageView songImageView;

    private boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String songUrl = getIntent().getStringExtra("songUrl");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String image_url = getIntent().getStringExtra("image_url");

        progressTextView = findViewById(R.id.tvProgressText);
        circleProgressBar = findViewById(R.id.circleProgressBar);
        songTitle = findViewById(R.id.songTitle);
        songDescription = findViewById(R.id.songDescription);
        songImageView = findViewById(R.id.songImageView);
        finishNowLink = findViewById(R.id.finishNowLink);

        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setImageResource(R.drawable.pause_button_ic);

        songTitle.setText(title);
        songDescription.setText(description);
        Picasso.get()
                .load(image_url)
                .into(songImageView);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startProgressUpdate();

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.play_button_for_player_ic);
                } else {
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.pause_button_ic);
                }
                isPlaying = !isPlaying;
            }
        });

        finishNowLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                builder.setTitle("Завершить сейчас?");
                builder.setMessage("Вы уверены, что хотите завершить воспроизведение и вернуться на главный экран?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            isPlaying = false;
                            finish();
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showFinishDialog();
            }
        });
    }
    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        builder.setTitle("Медитация завершена");
        builder.setMessage("Уделяйте больше времени своему ментальному здоровью. Мы будем напоминать вам об этом");

        builder.setPositiveButton("Выйти на главный экран", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void startProgressUpdate() {
        new Thread(() -> {
            while (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    runOnUiThread(() -> {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int totalDuration = mediaPlayer.getDuration();
                        int progress = (int) (currentPosition * 100.0 / totalDuration);
                        int secondsRemaining = (totalDuration - currentPosition) / 1000;
                        String timeRemaining = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60);

                        circleProgressBar.setProgress(progress);
                        progressTextView.setText(timeRemaining);
                    });
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            finish();
    }
}

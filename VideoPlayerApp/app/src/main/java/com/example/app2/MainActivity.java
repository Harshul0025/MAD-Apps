package com.example.app2;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnOpenFile, btnOpenURL, btnPlay, btnPause, btnStop, btnRestart;
    VideoView videoView;
    MediaPlayer mediaPlayer;
    Uri audioUri, videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenFile = findViewById(R.id.btnOpenFile);
        btnOpenURL = findViewById(R.id.btnOpenURL);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnRestart = findViewById(R.id.btnRestart);
        videoView = findViewById(R.id.videoView);

        // 🔹 Open audio file from storage
        btnOpenFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, 1);
        });

        // 🔹 Enter video URL
        btnOpenURL.setOnClickListener(v -> showUrlDialog());

        // 🔹 PLAY
        btnPlay.setOnClickListener(v -> {
            if (audioUri != null) {
                playAudio();
            } else if (videoUri != null) {
                videoView.start();
            }
        });

        // 🔹 PAUSE
        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        });

        // 🔹 STOP
        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        });

        // 🔹 RESTART
        btnRestart.setOnClickListener(v -> {
            if (audioUri != null) {
                playAudio();
            }
            if (videoUri != null) {
                videoView.start();
            }
        });
    }

    // 🔹 Handle file picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            audioUri = data.getData();
            Toast.makeText(this, "Audio Loaded", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔹 Play Audio
    private void playAudio() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, audioUri);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 URL Dialog
    private void showUrlDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter video URL");

        new AlertDialog.Builder(this)
                .setTitle("Video URL")
                .setView(input)
                .setPositiveButton("Play", (dialog, which) -> {
                    String url = input.getText().toString();
                    videoUri = Uri.parse(url);
                    videoView.setVideoURI(videoUri);
                    videoView.start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}


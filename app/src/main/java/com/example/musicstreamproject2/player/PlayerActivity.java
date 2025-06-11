package com.example.musicstreamproject2.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.databinding.ActivityPlayerBinding;
import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.services.MusicService;

public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    private MusicService musicService;
    private boolean isServiceBound = false;
    private Handler handler = new Handler();
    private SongModel currentSong;

    // Service connection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isServiceBound = true;

            // Connect PlayerView to ExoPlayer
            binding.playerView.setPlayer(musicService.getExoPlayer());

            // Set up player listener for UI updates
            musicService.getExoPlayer().addListener(new Player.Listener() {
                @Override
                public void onMediaItemTransition(androidx.media3.common.MediaItem mediaItem, int reason) {
                    // Update UI when song changes (for playlist support)
                    updateCurrentSongInfo();
                }
            });

            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get song data from intent
        currentSong = (SongModel) getIntent().getSerializableExtra("SONG");

        setupUI();
        startAndBindService();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupUI() {
        if (currentSong != null) {
            // Update UI with song info using correct IDs
            binding.songTitleTextView.setText(currentSong.getTitle());
            binding.songSubtitleTextView.setText(currentSong.getSubtitle());
            binding.nowPlayingText.setText("Now Playing");

            // Load cover image into both ImageViews
            Glide.with(this)
                    .load(currentSong.getCoverUrl())
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.songCoverImageView);

            Glide.with(this)
                    .load(currentSong.getCoverUrl())
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.songGifImageView);
        }

        // Configure PlayerView to use built-in controls
        binding.playerView.setUseController(true);
        binding.playerView.setShowBuffering(androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        binding.playerView.setControllerShowTimeoutMs(0); // Keep controls always visible
        binding.playerView.setControllerHideOnTouch(false); // Don't hide controls on touch
    }

    private void setupSeekBarIfExists() {
        // Not needed - PlayerView handles this automatically
    }

    private void setupBackButtonIfExists() {
        // Not needed - can be handled in XML or by system back button
    }

    private void startAndBindService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        // Start service as foreground service for background playback
        startForegroundService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateUI() {
        if (isServiceBound && currentSong != null) {
            // Check if we need to play a new song
            if (!isSameSong(musicService.getCurrentSong(), currentSong)) {
                musicService.playSong(currentSong);
            }
            updateCurrentSongInfo();
        }
    }

    private void updateCurrentSongInfo() {
        if (isServiceBound) {
            SongModel serviceSong = musicService.getCurrentSong();
            if (serviceSong != null) {
                currentSong = serviceSong;
                binding.songTitleTextView.setText(currentSong.getTitle());
                binding.songSubtitleTextView.setText(currentSong.getSubtitle());

                // Update cover images
                Glide.with(this)
                        .load(currentSong.getCoverUrl())
                        .placeholder(R.drawable.ic_music_note)
                        .into(binding.songCoverImageView);

                Glide.with(this)
                        .load(currentSong.getCoverUrl())
                        .placeholder(R.drawable.ic_music_note)
                        .into(binding.songGifImageView);
            }
        }
    }


    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private boolean isSameSong(SongModel song1, SongModel song2) {
        if (song1 == null || song2 == null) return false;
        return song1.getTitle().equals(song2.getTitle()) &&
                song1.getSubtitle().equals(song2.getSubtitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning to activity
        if (isServiceBound) {
            updateCurrentSongInfo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't stop music when activity goes to background
        // This enables Spotify-like background playback
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release PlayerView
        if (binding.playerView != null) {
            binding.playerView.setPlayer(null);
        }

        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }

        // Don't stop the service here - let it continue in background
    }

    @Override
    public void onBackPressed() {
        // Don't stop music when user presses back
        super.onBackPressed();
    }
}
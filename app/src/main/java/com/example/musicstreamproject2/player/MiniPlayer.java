package com.example.musicstreamproject2.player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.models.SongModel;

public class MiniPlayer {
    private View miniPlayerView;
    private Context context;
    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView artistTextView;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;

    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            // ExoPlayer handles UI updates automatically
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            updateServiceState(isPlaying);
        }
    };

    public MiniPlayer(Context context) {
        this.context = context;
        initializeViews();
        setupClickListeners();
        exoPlayer = MyExoPlayer.getInstance();
        if (exoPlayer != null) {
            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(playerListener);
        }
    }

    private void initializeViews() {
        LayoutInflater inflater = LayoutInflater.from(context);
        miniPlayerView = inflater.inflate(R.layout.mini_player, null);

        coverImageView = miniPlayerView.findViewById(R.id.mini_player_cover);
        titleTextView = miniPlayerView.findViewById(R.id.mini_player_title);
        artistTextView = miniPlayerView.findViewById(R.id.mini_player_artist);
        playerView = miniPlayerView.findViewById(R.id.mini_player_controls);
    }

    private void setupClickListeners() {
        miniPlayerView.setOnClickListener(v -> {
            // Open full player activity
            Intent intent = new Intent(context, PlayerActivity.class);
            context.startActivity(intent);
        });
    }

    private void updateServiceState(boolean isPlaying) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (isPlaying) {
            serviceIntent.setAction("RESUME");
        } else {
            serviceIntent.setAction("PAUSE");
        }
        context.startService(serviceIntent);
    }

    public void updateSong(SongModel song) {
        if (song != null) {
            titleTextView.setText(song.getTitle());
            artistTextView.setText(song.getSubtitle());

            Glide.with(context)
                    .load(song.getCoverUrl())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(coverImageView);
        }
    }

    public void updatePlayer() {
        exoPlayer = MyExoPlayer.getInstance();
        if (exoPlayer != null && playerView != null) {
            playerView.setPlayer(exoPlayer);
            exoPlayer.addListener(playerListener);
        }
    }

    public View getView() {
        return miniPlayerView;
    }

    public void showMiniPlayer() {
        if (miniPlayerView != null) {
            miniPlayerView.setVisibility(View.VISIBLE);
        }
    }

    public void hideMiniPlayer() {
        if (miniPlayerView != null) {
            miniPlayerView.setVisibility(View.GONE);
        }
    }

    public void destroy() {
        if (exoPlayer != null) {
            exoPlayer.removeListener(playerListener);
        }
    }
}

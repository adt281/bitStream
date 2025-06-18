package com.example.musicstreamproject2.player;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.databinding.ActivityPlayerBinding;
import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.BaseActivity;

public class PlayerActivity extends BaseActivity {

    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;
    private SongModel songModel;

    // 🔹 Define Player.Listener
    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    // Show loading UI if needed
                    break;
                case Player.STATE_READY:
                    // Hide loading, ready to play
                    break;
                case Player.STATE_ENDED:
                    // Handle when song finishes
                    break;
                case Player.STATE_IDLE:
                    // No media
                    break;
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            // Handle error
            error.printStackTrace();
        }
    };

    @OptIn(markerClass = UnstableApi.class)
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

        songModel = MyExoPlayer.getCurrentSong();
        if (songModel != null) {
            binding.songTitleTextView.setText(songModel.getTitle());
            binding.songSubtitleTextView.setText(songModel.getSubtitle());

            RequestBuilder<Drawable> thumbnailRequest = Glide.with(this)
                    .load(songModel.getCoverUrl())
                    .sizeMultiplier(0.01f);

            Glide.with(this)
                    .load(songModel.getCoverUrl())
                    .thumbnail(thumbnailRequest)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.songCoverImageView);

            exoPlayer = MyExoPlayer.getInstance();
            if (exoPlayer != null) {
                binding.playerView.setPlayer(exoPlayer);
                binding.playerView.showController();
                exoPlayer.addListener(playerListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.removeListener(playerListener); // Clean up
        }
    }
}

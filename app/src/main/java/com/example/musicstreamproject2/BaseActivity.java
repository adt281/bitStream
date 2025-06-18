package com.example.musicstreamproject2;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.player.MiniPlayer;
import com.example.musicstreamproject2.player.MyExoPlayer;

public class BaseActivity extends AppCompatActivity {
    private MiniPlayer miniPlayer;
    private FrameLayout miniPlayerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupMiniPlayer();
    }

    private void setupMiniPlayer() {
        // Find mini player container in the layout
        miniPlayerContainer = findViewById(R.id.mini_player_container);

        if (miniPlayerContainer != null) {
            miniPlayer = new MiniPlayer(this);
            miniPlayerContainer.addView(miniPlayer.getView());

            // Update mini player if there's a current song
            SongModel currentSong = MyExoPlayer.getCurrentSong();
            if (currentSong != null) {
                showMiniPlayer(currentSong);
            } else {
                hideMiniPlayer();
            }
        }
    }

    public void showMiniPlayer(SongModel song) {
        if (miniPlayer != null && miniPlayerContainer != null) {
            miniPlayer.updateSong(song);
            miniPlayer.updatePlayer();
            miniPlayer.showMiniPlayer();
            miniPlayerContainer.setVisibility(ViewGroup.VISIBLE);
        }
    }

    public void hideMiniPlayer() {
        if (miniPlayer != null && miniPlayerContainer != null) {
            miniPlayer.hideMiniPlayer();
            miniPlayerContainer.setVisibility(ViewGroup.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update mini player state when activity resumes
        if (miniPlayer != null) {
            SongModel currentSong = MyExoPlayer.getCurrentSong();
            if (currentSong != null) {
                miniPlayer.updateSong(currentSong);
                miniPlayer.updatePlayer();
                showMiniPlayer(currentSong);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (miniPlayer != null) {
            miniPlayer.destroy();
        }
    }
}
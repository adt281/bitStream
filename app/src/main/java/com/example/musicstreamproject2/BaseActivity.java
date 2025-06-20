/**
  BaseActivity serves as a common parent activity for all activities in the app
  that want to include a persistent mini music player (MiniPlayer) at the bottom.

 * Key responsibilities:
  - Initializes and embeds the MiniPlayer view inside a container defined by the layout.
  - Automatically handles showing/hiding the MiniPlayer based on the current playing song.
  - Updates the MiniPlayer when the activity resumes.
  - Manages MiniPlayer lifecycle (initialization and cleanup).

  TODO: (IMP)  Any activity extending BaseActivity must include a FrameLayout with the ID 'mini_player_container'
        in its layout to correctly display the MiniPlayer.


 main purpose is so that in each activity you wont need to write code for infalting miniplayer. You just do "extend baseActivity"
 */



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
        miniPlayerContainer = findViewById(R.id.mini_player_container);

        if (miniPlayerContainer != null) {
            miniPlayer = new MiniPlayer(this);
            miniPlayerContainer.addView(miniPlayer.getView());

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

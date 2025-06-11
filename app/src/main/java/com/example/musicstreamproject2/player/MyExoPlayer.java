package com.example.musicstreamproject2.player;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicstreamproject2.models.SongModel;

public class MyExoPlayer {
    ExoPlayer exoPlayer=null;
    SongModel currentSongModel=null;

    public MyExoPlayer(ExoPlayer exoPlayer)
    {
        this.exoPlayer= exoPlayer;
    }
    public MyExoPlayer()
    {

    }
    public ExoPlayer getInstance()
    {
        return exoPlayer;
    }

    public void startPlaying(Context context, SongModel currentSongModel) {

        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        //so that tapping on the same song doesnt restart it
        if(this.currentSongModel==null || this.currentSongModel!=currentSongModel) {
            this.currentSongModel = currentSongModel;

            String url = currentSongModel.getUrl();
            if (url != null) {
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        }
    }



}

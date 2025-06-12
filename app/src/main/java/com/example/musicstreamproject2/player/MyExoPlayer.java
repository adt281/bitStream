package com.example.musicstreamproject2.player;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicstreamproject2.models.SongModel;

public class MyExoPlayer {

    private static ExoPlayer exoPlayer = null;
    private static SongModel currentSong = null;

    private MyExoPlayer() {
        // private constructor to prevent instantiation
    }

    public static SongModel getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        if (currentSong == null || !currentSong.equals(song)) {
            currentSong = song;

            String url = currentSong.getUrl();
            if (url != null) {
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        }
    }
}
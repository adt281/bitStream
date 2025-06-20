/**
 MyExoPlayer is a singleton-like helper class that manages a shared instance of ExoPlayer
 for playing audio throughout the app.

 Key Features:
 - Maintains a single ExoPlayer instance across activities for efficient resource use.
 - Keeps track of the currently playing song (SongModel).
 - Provides utility methods to start, pause, resume, and release playback.
 - Automatically prepares and plays a MediaItem when a new song is started.
 - Starts MusicService in foreground when playback begins.
 - Notifies BaseActivity (if context is an instance) to show the MiniPlayer.

 Typical Usage:
 - Call MyExoPlayer.getInstance(context) to initialize or retrieve the player.
 - Call startPlaying(context, song) to begin playback of a song.
 - Other components like MiniPlayer or activities can use pausePlayer(), resumePlayer(), or isPlaying()
 to control or check playback status.
 */





package com.example.musicstreamproject2.player;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicstreamproject2.BaseActivity;
import com.example.musicstreamproject2.models.SongModel;

public class MyExoPlayer {
    private static ExoPlayer exoPlayer = null;
    private static SongModel currentSong = null;

    public interface OnSongChangeListener {
        void onSongChanged(SongModel song);
    }

    private MyExoPlayer() {
        // private constructor to prevent instantiation
    }

    public static SongModel getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance(Context context) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context)
                    .setAudioAttributes(AudioAttributes.DEFAULT, true)
                    .setHandleAudioBecomingNoisy(true)
                    .setWakeMode(C.WAKE_MODE_LOCAL)
                    .build();
        }
        return exoPlayer;
    }

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = getInstance(context);
        }

        if (currentSong == null || !currentSong.equals(song)) {
            currentSong = song;

            String url = currentSong.getUrl();
            if (url != null) {
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
                exoPlayer.setMediaItem(mediaItem);

                exoPlayer.setPlaybackParameters(new PlaybackParameters(1f));
                exoPlayer.prepare();
                exoPlayer.play();

                // Start background service
                Intent serviceIntent = new Intent(context, MusicService.class);
                serviceIntent.setAction("PLAY");
                context.startForegroundService(serviceIntent);

                // Notify BaseActivity to show mini player
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).showMiniPlayer(currentSong);
                }
            }
        }
    }

    public static void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    public static void resumePlayer() {
        if (exoPlayer != null) {
            exoPlayer.play();
        }
    }

    public static boolean isPlaying() {
        return exoPlayer != null && exoPlayer.isPlaying();
    }

    public static void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
            currentSong = null;
        }
    }
}

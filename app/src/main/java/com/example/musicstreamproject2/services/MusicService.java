package com.example.musicstreamproject2.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.models.SongModel;
import com.example.musicstreamproject2.player.PlayerActivity;

public class MusicService extends MediaSessionService {

    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private ExoPlayer exoPlayer;
    private MediaSession mediaSession;
    private SongModel currentSong;

    // Binder for activity communication
    private final IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializePlayer();
        createNotificationChannel();
    }

    private void initializePlayer() {
        // Initialize ExoPlayer
        exoPlayer = new ExoPlayer.Builder(this)
                .build();

        // Set up player listeners
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                updateNotification();
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updateNotification();
                if (isPlaying) {
                    startForegroundService();
                }
            }
        });

        // Create and configure MediaSession
        mediaSession = new MediaSession.Builder(this, exoPlayer)
                .build();
    }

    // Fixed: Only one onGetSession method
    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    // Public methods for controlling playback
    public void playSong(SongModel song) {
        if (song == null || song.getUrl() == null) return;

        // Don't restart if same song is already playing
        if (currentSong != null && currentSong.equals(song) && exoPlayer.isPlaying()) {
            return;
        }

        currentSong = song;

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(song.getUrl()));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        updateNotification();
    }

    public void playPause() {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
        } else {
            exoPlayer.play();
        }
    }

    public void seekTo(long position) {
        exoPlayer.seekTo(position);
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public SongModel getCurrentSong() {
        return currentSong;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Controls for music playback");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundService() {
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void updateNotification() {
        if (currentSong != null) {
            Notification notification = createNotification();
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, notification);
            }
        }
    }

    private Notification createNotification() {
        // Intent to open PlayerActivity when notification is clicked
        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Play/Pause action
        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction("ACTION_PLAY_PAUSE");
        PendingIntent playPausePendingIntent = PendingIntent.getService(
                this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE
        );

        String title = currentSong != null ? currentSong.getTitle() : "Unknown";
        String artist = currentSong != null ? currentSong.getSubtitle() : "Unknown Artist";

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(artist)
                .setSmallIcon(R.drawable.ic_music_note) // Add this icon to drawable
                .setContentIntent(pendingIntent)
                .addAction(
                        exoPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play,
                        exoPlayer.isPlaying() ? "Pause" : "Play",
                        playPausePendingIntent
                )
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionExtras()))
                        .setShowActionsInCompactView(0))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "ACTION_PLAY_PAUSE":
                    playPause();
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.release();
        }
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        super.onDestroy();
    }
}
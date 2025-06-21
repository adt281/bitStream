package com.example.musicstreamproject2.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musicstreamproject2.MainActivity;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.models.SongModel;

public class MusicService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MusicPlayerChannel";

    private ExoPlayer exoPlayer;
    private Handler progressHandler;
    private Runnable progressRunnable;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        exoPlayer = MyExoPlayer.getInstance(this);

        // Initialize MediaSession
        setupMediaSession();

        // Initialize progress handler
        progressHandler = new Handler(Looper.getMainLooper());
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (MyExoPlayer.isPlaying()) {
                    updateNotification();
                }
                progressHandler.postDelayed(this, 1000); // Update every second
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(this, "MusicServiceSession");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set up MediaSession callback to handle button clicks
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                MyExoPlayer.resumePlayer();
                updateNotification();
            }

            @Override
            public void onPause() {
                MyExoPlayer.pausePlayer();
                updateNotification();
            }

            @Override
            public void onSkipToNext() {
                handleNext();
                updateNotification();
            }

            @Override
            public void onSkipToPrevious() {
                handlePrevious();
                updateNotification();
            }

            @Override
            public void onStop() {
                MyExoPlayer.pausePlayer();
                stopForeground(true);
                stopSelf();
            }
        });

        mediaSession.setActive(true);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;

        if ("PLAY".equals(action) || "RESUME".equals(action) ||
                "NEXT".equals(action) || "PREVIOUS".equals(action)) {
            startForeground(NOTIFICATION_ID, createNotification());
        }

        switch (action) {
            case "PAUSE":
                MyExoPlayer.pausePlayer();
                startForeground(NOTIFICATION_ID, createNotification()); // Ensure it updates
                break;
            case "RESUME":
                MyExoPlayer.resumePlayer();
                startForeground(NOTIFICATION_ID, createNotification());
                break;
            case "NEXT":
                handleNext();
                startForeground(NOTIFICATION_ID, createNotification());
                break;
            case "PREVIOUS":
                handlePrevious();
                startForeground(NOTIFICATION_ID, createNotification());
                break;
            case "STOP":
                MyExoPlayer.pausePlayer();
                stopForeground(true);
                stopSelf();
                break;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            channel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        SongModel currentSong = MyExoPlayer.getCurrentSong();

        // Add null checks and default values
        String title = "Music Player";
        String artist = "Unknown Artist";

        if (currentSong != null) {
            title = currentSong.getTitle() != null && !currentSong.getTitle().isEmpty()
                    ? currentSong.getTitle() : "Unknown Title";
            artist = currentSong.getSubtitle() != null && !currentSong.getSubtitle().isEmpty()
                    ? currentSong.getSubtitle() : "Unknown Artist";
        }

        // Update playback state for MediaSession
        updatePlaybackState();

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create action intents
        PendingIntent previousIntent = createActionIntent("PREVIOUS", 1);
        PendingIntent playPauseIntent = createActionIntent(MyExoPlayer.isPlaying() ? "PAUSE" : "RESUME", 2);
        PendingIntent nextIntent = createActionIntent("NEXT", 3);
        PendingIntent stopIntent = createActionIntent("STOP", 4);

        // Create custom expanded view with progress bar
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_large);

        // Set content for expanded layout
        expandedView.setTextViewText(R.id.notification_title, title);
        expandedView.setTextViewText(R.id.notification_artist, artist);

        // Set button actions for expanded layout
        expandedView.setOnClickPendingIntent(R.id.btn_previous, previousIntent);
        expandedView.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent);
        expandedView.setOnClickPendingIntent(R.id.btn_next, nextIntent);
        expandedView.setOnClickPendingIntent(R.id.btn_stop, stopIntent);

        // Set play/pause button icon
        int playPauseIcon = MyExoPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;
        expandedView.setImageViewResource(R.id.btn_play_pause, playPauseIcon);

        // Album art removed - add ImageView with id="album_art" to notification_large.xml if you want it

        // Update progress bar if playing
        if (exoPlayer != null) {
            long duration = exoPlayer.getDuration();
            long position = exoPlayer.getCurrentPosition();
            if (duration > 0) {
                int progress = (int) ((position * 100) / duration);
                expandedView.setProgressBar(R.id.progress_bar, 100, progress, false);

                // Optional: Set time text if you have TextView for it
                String currentTime = formatTime(position);
                String totalTime = formatTime(duration);
                // expandedView.setTextViewText(R.id.current_time, currentTime);
                // expandedView.setTextViewText(R.id.total_time, totalTime);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(artist)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add media control actions for compact view
                .addAction(R.drawable.ic_skip_previous, "Previous", previousIntent)
                .addAction(MyExoPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play,
                        MyExoPlayer.isPlaying() ? "Pause" : "Play", playPauseIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextIntent)

                // Custom expanded view with progress bar
                .setCustomBigContentView(expandedView)

                // Use MediaStyle for proper media controls integration
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)); // Show all three buttons in compact view

        // Set large icon (album art) for compact view if available
        if (currentSong != null && currentSong.getCoverUrl() != null) {
            // TODO: Set large icon bitmap here when you implement image loading
            // builder.setLargeIcon(albumArtBitmap);
        }

        return builder.build();
    }

    private void updatePlaybackState() {
        if (mediaSession != null) {
            PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_STOP)
                    .setState(MyExoPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                            exoPlayer != null ? exoPlayer.getCurrentPosition() : 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(playbackState);
        }
    }

    private PendingIntent createActionIntent(String action, int requestCode) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(
                this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void updateNotification() {
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);  // This updates the notification
    }


    private void handleNext() {
        // TODO: Implement next song logic when queue system is ready
        System.out.println("Next song requested - Queue system not implemented yet");
    }

    private void handlePrevious() {
        // TODO: Implement previous song logic when queue system is ready
        System.out.println("Previous song requested - Queue system not implemented yet");
    }

    // Helper method to format time
    private String formatTime(long timeMs) {
        long seconds = timeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressHandler != null && progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
        }
        MyExoPlayer.releasePlayer();
    }
}
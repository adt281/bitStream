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

        if ("PLAY".equals(action)) {
            startForeground(NOTIFICATION_ID, createNotification());
        } else if ("PAUSE".equals(action)) {
            MyExoPlayer.pausePlayer();
            updateNotification();
        } else if ("RESUME".equals(action)) {
            MyExoPlayer.resumePlayer();
            updateNotification();
        } else if ("NEXT".equals(action)) {
            handleNext();
            updateNotification();
        } else if ("PREVIOUS".equals(action)) {
            handlePrevious();
            updateNotification();
        } else if ("STOP".equals(action)) {
            MyExoPlayer.pausePlayer();
            stopForeground(true);
            stopSelf();
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
        String title = currentSong != null ? currentSong.getTitle() : "Music Player";
        String artist = currentSong != null ? currentSong.getSubtitle() : "Unknown Artist";

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

        // Create custom notification layout
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

        // Set content for both layouts
        notificationLayout.setTextViewText(R.id.notification_title, title);
        notificationLayout.setTextViewText(R.id.notification_artist, artist);
        notificationLayoutExpanded.setTextViewText(R.id.notification_title, title);
        notificationLayoutExpanded.setTextViewText(R.id.notification_artist, artist);

        // Set button actions for small layout
        notificationLayout.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent);
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextIntent);

        // Set button actions for expanded layout
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_previous, previousIntent);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_next, nextIntent);
        notificationLayoutExpanded.setOnClickPendingIntent(R.id.btn_stop, stopIntent);

        // Set play/pause button icon
        int playPauseIcon = MyExoPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;
        notificationLayout.setImageViewResource(R.id.btn_play_pause, playPauseIcon);
        notificationLayoutExpanded.setImageViewResource(R.id.btn_play_pause, playPauseIcon);

        // Update progress bar if playing
        if (exoPlayer != null) {
            long duration = exoPlayer.getDuration();
            long position = exoPlayer.getCurrentPosition();
            if (duration > 0) {
                int progress = (int) ((position * 100) / duration);
                notificationLayoutExpanded.setProgressBar(R.id.progress_bar, 100, progress, false);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_blank)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Add MediaStyle for better media controls integration
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(1) // Show play/pause in compact view
                );

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
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification());
        }
    }

    private void handleNext() {
        // TODO: Implement next song logic when queue system is ready
        // For now, just a placeholder
        System.out.println("Next song requested - Queue system not implemented yet");
    }

    private void handlePrevious() {
        // TODO: Implement previous song logic when queue system is ready
        // For now, just a placeholder
        System.out.println("Previous song requested - Queue system not implemented yet");
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
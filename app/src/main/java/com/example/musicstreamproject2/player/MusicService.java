package com.example.musicstreamproject2.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

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

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        exoPlayer = MyExoPlayer.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if ("PLAY".equals(action)) {
            startForeground(NOTIFICATION_ID, createNotification());
        } else if ("PAUSE".equals(action)) {
            MyExoPlayer.pausePlayer();
            updateNotification();
        } else if ("RESUME".equals(action)) {
            MyExoPlayer.resumePlayer();
            updateNotification();
        } else if ("STOP".equals(action)) {
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
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        SongModel currentSong = MyExoPlayer.getCurrentSong();
        String title = currentSong != null ? currentSong.getTitle() : "Music Player";
        String artist = currentSong != null ? currentSong.getSubtitle() : "Unknown Artist";

        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("RESUME");
        PendingIntent resumePendingIntent = PendingIntent.getService(
                this, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(artist)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (MyExoPlayer.isPlaying()) {
            builder.addAction(R.drawable.logo, "Pause", pausePendingIntent);
        } else {
            builder.addAction(R.drawable.logo, "Play", resumePendingIntent);
        }

        return builder.build();
    }

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, createNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyExoPlayer.releasePlayer();
    }
}

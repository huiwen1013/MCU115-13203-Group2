package com.example.findbackapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class MatchingService extends Service {

    private static final String TAG = "MatchingService";
    private static final String CHANNEL_ID = "MatchingServiceChannel";
    private static final int NOTIFICATION_ID = 1001;

    private DatabaseHelper dbHelper;
    private MatchingManager matchingManager;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            dbHelper = new DatabaseHelper(this);
            matchingManager = new MatchingManager(dbHelper);
            createNotificationChannel();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MatchingService started");

        try {
            createNotificationChannel();
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("FindBack 智慧媒合系統")
                    .setContentText("正在背景計算最新遺失與拾獲物品相符度…")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            Log.e(TAG, "Error starting foreground notification", e);
        }

        // Perform matching calculation in background thread
        new Thread(() -> {
            try {
                if (matchingManager != null) {
                    List<MatchingResult> matches = matchingManager.findAllMatches();
                    Log.d(TAG, "Background matching finished. Total matches found: " + matches.size());

                    Intent broadcastIntent = new Intent("com.example.findbackapp.MATCHING_UPDATED");
                    broadcastIntent.putExtra("match_count", matches.size());
                    sendBroadcast(broadcastIntent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error performing background matching", e);
            } finally {
                stopSelf();
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "智慧媒合背景服務",
                        NotificationManager.IMPORTANCE_LOW
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(serviceChannel);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed creating notification channel", e);
            }
        }
    }
}

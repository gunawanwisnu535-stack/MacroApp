package com.example.macroapp.service;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.macroapp.R;
import com.example.macroapp.model.GestureEvent;
import com.example.macroapp.model.MacroRecording;
import com.example.macroapp.storage.MacroStorageManager;

/**
 * MacroRecordingService - Service untuk merekam gesture actions
 * 
 * Fungsi:
 * - Monitor accessibility events
 * - Record gesture data (tap, swipe, long press)
 * - Save recordings ke storage
 * - Provide foreground notification untuk Android 8+
 */
public class MacroRecordingService extends Service {

    private static final String TAG = "MacroRecordingService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "macro_recording_channel";
    private static final String ACTION_START = "START_RECORDING";
    private static final String ACTION_STOP = "STOP_RECORDING";

    private boolean isRecording = false;
    private MacroRecording currentRecording;
    private MacroStorageManager storageManager;
    private NotificationManager notificationManager;
    private long recordingStartTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RecordingService onCreate");
        
        // Initialize storage manager
        storageManager = new MacroStorageManager(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Create notification channel untuk Android 8+
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand - intent action: " + (intent != null ? intent.getAction() : "null"));
        
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    startRecording();
                    break;
                case ACTION_STOP:
                    stopRecording();
                    break;
                default:
                    // Default behavior jika tidak ada action
                    if (!isRecording) {
                        startRecording();
                    }
            }
        } else {
            // Default: start recording
            if (!isRecording) {
                startRecording();
            }
        }
        
        return START_STICKY;
    }

    /**
     * Mulai recording gesture events
     */
    private void startRecording() {
        if (isRecording) {
            Log.w(TAG, "Recording already active");
            return;
        }

        try {
            isRecording = true;
            recordingStartTime = System.currentTimeMillis();
            currentRecording = new MacroRecording("Recording_" + recordingStartTime);
            
            Log.d(TAG, "✓ Recording started");
            updateNotification("🔴 Recording...", true);
        } catch (Exception e) {
            Log.e(TAG, "Error starting recording", e);
            isRecording = false;
            updateNotification("❌ Error starting recording", false);
        }
    }

    /**
     * Hentikan recording dan simpan
     */
    private void stopRecording() {
        if (!isRecording) {
            Log.w(TAG, "No active recording to stop");
            return;
        }

        try {
            isRecording = false;
            
            // Finalize recording
            if (currentRecording != null) {
                currentRecording.finalize();
                
                // Save to storage
                boolean saved = storageManager.saveMacroRecording(currentRecording);
                
                if (saved) {
                    Log.d(TAG, "✓ Recording stopped and saved: " + currentRecording.getSummary());
                    updateNotification("✅ Recording saved", false);
                } else {
                    Log.w(TAG, "✗ Failed to save recording");
                    updateNotification("❌ Failed to save recording", false);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping recording", e);
            updateNotification("❌ Error stopping recording", false);
        }
    }

    /**
     * Record gesture event (dipanggil dari MainActivity via callback)
     * 
     * @param gestureEvent Gesture event yang ingin direkam
     */
    public synchronized void recordGesture(GestureEvent gestureEvent) {
        if (!isRecording || currentRecording == null) {
            Log.w(TAG, "Recording not active, ignoring gesture");
            return;
        }

        try {
            currentRecording.addGesture(gestureEvent);
            Log.d(TAG, "✓ Gesture recorded: " + gestureEvent.getTypeString() + 
                    " (Total: " + currentRecording.getGestureCount() + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error recording gesture", e);
        }
    }

    /**
     * Get current recording status
     */
    public boolean isCurrentlyRecording() {
        return isRecording;
    }

    /**
     * Get current recording object
     */
    public MacroRecording getCurrentRecording() {
        return currentRecording;
    }

    /**
     * Create notification channel untuk Android 8+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Macro Recording",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notification for macro recording service");
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }

    /**
     * Update foreground notification
     */
    private void updateNotification(String text, boolean isRecording) {
        try {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MacroApp")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(isRecording)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            
            // Gunakan startForeground untuk Android 8+
            if (isRecording) {
                startForeground(NOTIFICATION_ID, notification);
            } else {
                // Stop foreground tetapi update notification
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating notification", e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RecordingService onDestroy");
        
        // Stop recording jika masih active
        if (isRecording) {
            stopRecording();
        }
        
        // Stop foreground service
        stopForeground(true);
    }
}

package com.example.macroapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.macroapp.service.MacroRecordingService;
import com.example.macroapp.service.MacroAccessibilityService;
import com.example.macroapp.storage.MacroStorageManager;
import com.example.macroapp.model.MacroRecording;

/**
 * MainActivity - Activity utama untuk MacroApp
 * 
 * Fungsi:
 * - Manage recording dan playback macro
 * - Check accessibility service status
 * - Kontrol UI dan user interactions
 * - Handle macro persistence
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String ACCESSIBILITY_SERVICE_NAME = "com.example.macroapp/com.example.macroapp.service.MacroAccessibilityService";

    private Button btnStartRecording, btnStopRecording, btnPlayback;
    private TextView tvStatus;
    private boolean isRecording = false;
    private MacroStorageManager storageManager;
    private RecordingStateListener recordingStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity created");

        // Initialize storage manager
        storageManager = new MacroStorageManager(this);

        // Initialize UI components
        initializeUI();

        // Check Accessibility Service on startup
        checkAndPromptAccessibilityService();

        // Set button listeners
        setupButtonListeners();
    }

    /**
     * Initialize UI components
     */
    private void initializeUI() {
        btnStartRecording = findViewById(R.id.btn_start_recording);
        btnStopRecording = findViewById(R.id.btn_stop_recording);
        btnPlayback = findViewById(R.id.btn_playback);
        tvStatus = findViewById(R.id.tv_status);

        // Validasi UI components
        if (btnStartRecording == null || btnStopRecording == null || 
            btnPlayback == null || tvStatus == null) {
            Log.e(TAG, "Some UI components not found in layout");
            Toast.makeText(this, "Error: Layout components not found", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setup click listeners untuk semua buttons
     */
    private void setupButtonListeners() {
        if (btnStartRecording != null) {
            btnStartRecording.setOnClickListener(v -> startRecording());
        }
        
        if (btnStopRecording != null) {
            btnStopRecording.setOnClickListener(v -> stopRecording());
        }
        
        if (btnPlayback != null) {
            btnPlayback.setOnClickListener(v -> playRecordedMacro());
        }
    }

    /**
     * Check accessibility service dan prompt user jika belum aktif
     */
    private void checkAndPromptAccessibilityService() {
        if (isAccessibilityServiceEnabled()) {
            updateStatus("✅ Service siap digunakan", true);
            Log.d(TAG, "Accessibility Service is enabled");
        } else {
            updateStatus("⚠️ Accessibility Service belum diaktifkan", false);
            Log.w(TAG, "Accessibility Service is not enabled");
            showAccessibilityPrompt();
        }
    }

    /**
     * Mulai merekam macro gesture
     */
    private void startRecording() {
        // Validasi state
        if (isRecording) {
            Toast.makeText(this, "Recording sudah berjalan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check accessibility service
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "❌ Aktifkan Accessibility Service terlebih dahulu", Toast.LENGTH_SHORT).show();
            showAccessibilityPrompt();
            return;
        }

        try {
            isRecording = true;
            Intent serviceIntent = new Intent(this, MacroRecordingService.class);
            serviceIntent.setAction("START_RECORDING");
            
            // Use foreground service untuk Android 8+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            updateStatus("🔴 Recording...", true);
            Log.d(TAG, "Recording started");
            Toast.makeText(this, "🎥 Recording dimulai - lakukan gesture yang ingin direkam", Toast.LENGTH_SHORT).show();
            
            // Notify listener
            if (recordingStateListener != null) {
                recordingStateListener.onRecordingStarted();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting recording", e);
            isRecording = false;
            updateStatus("❌ Error saat memulai recording", false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hentikan recording
     */
    private void stopRecording() {
        if (!isRecording) {
            Toast.makeText(this, "Tidak ada recording yang sedang berjalan", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            isRecording = false;
            Intent serviceIntent = new Intent(this, MacroRecordingService.class);
            serviceIntent.setAction("STOP_RECORDING");
            stopService(serviceIntent);

            updateStatus("⏸️ Recording dihentikan - siap untuk playback", true);
            Log.d(TAG, "Recording stopped");
            Toast.makeText(this, "⏹️ Recording selesai", Toast.LENGTH_SHORT).show();
            
            // Notify listener
            if (recordingStateListener != null) {
                recordingStateListener.onRecordingStopped();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping recording", e);
            updateStatus("❌ Error saat menghentikan recording", false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Putar macro yang sudah direkam
     */
    private void playRecordedMacro() {
        // Validasi recording state
        if (isRecording) {
            Toast.makeText(this, "❌ Hentikan recording terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi accessibility service
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "❌ Accessibility Service tidak aktif", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check apakah ada macro yang tersimpan
        if (!storageManager.hasSavedMacro()) {
            Toast.makeText(this, "❌ Tidak ada macro yang tersimpan. Silakan record terlebih dahulu", Toast.LENGTH_SHORT).show();
            updateStatus("⚠️ Tidak ada macro untuk diputar", false);
            return;
        }

        try {
            updateStatus("▶️ Memutar macro...", true);
            Log.d(TAG, "Playback started");

            // Load macro dari storage
            MacroRecording savedRecording = storageManager.loadMacroRecording();
            if (savedRecording == null) {
                Toast.makeText(this, "❌ Gagal memuat macro", Toast.LENGTH_SHORT).show();
                updateStatus("❌ Error memuat macro", false);
                return;
            }

            // Show confirmation dialog dengan info macro
            String macroInfo = String.format("Macro: %s\nGestures: %d\nDurasi: %.1f detik",
                    savedRecording.getName(),
                    savedRecording.getGestureCount(),
                    savedRecording.getRecordingDuration() / 1000.0);

            new AlertDialog.Builder(this)
                    .setTitle("Playback Macro")
                    .setMessage("Macro akan dijalankan. Pastikan Anda sudah di aplikasi target.\n\n" + macroInfo)
                    .setPositiveButton("Jalankan", (dialog, which) -> {
                        // TODO: Implement actual macro playback dengan MacroAccessibilityService
                        // For now, just show a message
                        Toast.makeText(MainActivity.this, "▶️ Playback dimulai", Toast.LENGTH_SHORT).show();
                        updateStatus("▶️ Playback selesai", true);
                        
                        // Notify listener
                        if (recordingStateListener != null) {
                            recordingStateListener.onPlaybackStarted();
                        }
                    })
                    .setNegativeButton("Batal", (dialog, which) -> {
                        updateStatus("✅ Playback dibatalkan", true);
                    })
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error playing macro", e);
            updateStatus("❌ Error saat memutar macro", false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check apakah Accessibility Service sudah diaktifkan
     * 
     * @return true jika MacroAccessibilityService enabled
     */
    private boolean isAccessibilityServiceEnabled() {
        try {
            int accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED,
                    0
            );

            if (accessibilityEnabled == 1) {
                String enabledServices = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                );

                if (enabledServices != null) {
                    // Check for full package name
                    return enabledServices.contains(ACCESSIBILITY_SERVICE_NAME) || 
                           enabledServices.contains("MacroAccessibilityService");
                }
            }
            return false;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error checking accessibility service", e);
            return false;
        }
    }

    /**
     * Show dialog untuk enable Accessibility Service
     */
    private void showAccessibilityPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Accessibility Service Diperlukan")
                .setMessage("MacroApp memerlukan Accessibility Service untuk merekam dan memutar gesture. " +
                        "Silakan aktifkan di Settings > Accessibility.")
                .setPositiveButton("Buka Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Nanti", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, 
                            "⚠️ Accessibility Service wajib diaktifkan untuk menggunakan fitur ini", 
                            Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Update status text di UI dengan formatting
     * 
     * @param status Status text to display
     * @param isSuccess Whether the status is successful (green) or error (red)
     */
    private void updateStatus(String status, boolean isSuccess) {
        if (tvStatus != null) {
            tvStatus.setText(status);
            // TODO: Add color change untuk success/error state
            // if (isSuccess) {
            //     tvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            // } else {
            //     tvStatus.setTextColor(getColor(android.R.color.holo_red_dark));
            // }
        }
    }

    /**
     * Get current recording state
     */
    public boolean isCurrentlyRecording() {
        return isRecording;
    }

    /**
     * Set recording state listener
     */
    public void setRecordingStateListener(RecordingStateListener listener) {
        this.recordingStateListener = listener;
    }

    /**
     * Lifecycle: On resume - refresh accessibility service status
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - checking accessibility service status");
        
        // Re-check accessibility service in case user enabled it in settings
        if (!isRecording) {
            checkAndPromptAccessibilityService();
        }
    }

    /**
     * Lifecycle: On pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    /**
     * Lifecycle: On destroy - cleanup
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy - cleanup");
        
        // Stop recording jika masih berjalan
        if (isRecording) {
            stopRecording();
        }
    }

    /**
     * Interface untuk listen ke recording state changes
     * (Future enhancement untuk advanced features)
     */
    public interface RecordingStateListener {
        void onRecordingStarted();
        void onRecordingStopped();
        void onPlaybackStarted();
        void onPlaybackCompleted();
    }
}

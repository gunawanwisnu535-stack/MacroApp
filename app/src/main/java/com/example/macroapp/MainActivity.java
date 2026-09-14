package com.example.macroapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.macroapp.service.MacroRecordingService;

public class MainActivity extends AppCompatActivity {

    private Button btnStartRecording, btnStopRecording, btnPlayback;
    private TextView tvStatus;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI
        btnStartRecording = findViewById(R.id.btn_start_recording);
        btnStopRecording = findViewById(R.id.btn_stop_recording);
        btnPlayback = findViewById(R.id.btn_playback);
        tvStatus = findViewById(R.id.tv_status);

        // Check Accessibility Service
        if (!isAccessibilityServiceEnabled()) {
            tvStatus.setText("⚠️ Accessibility Service belum diaktifkan");
            showAccessibilityPrompt();
        } else {
            tvStatus.setText("✅ Service siap digunakan");
        }

        // Button Listeners
        btnStartRecording.setOnClickListener(v -> startRecording());
        btnStopRecording.setOnClickListener(v -> stopRecording());
        btnPlayback.setOnClickListener(v -> playRecordedMacro());
    }

    private void startRecording() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Aktifkan Accessibility Service terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        isRecording = true;
        Intent serviceIntent = new Intent(this, MacroRecordingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        tvStatus.setText("🔴 Recording...");
        Toast.makeText(this, "Recording dimulai", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        isRecording = false;
        stopService(new Intent(this, MacroRecordingService.class));
        tvStatus.setText("⏸️ Recording dihentikan");
        Toast.makeText(this, "Recording selesai", Toast.LENGTH_SHORT).show();
    }

    private void playRecordedMacro() {
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Accessibility Service tidak aktif", Toast.LENGTH_SHORT).show();
            return;
        }
        tvStatus.setText("▶️ Memutar macro...");
        Toast.makeText(this, "Playback dimulai", Toast.LENGTH_SHORT).show();
    }

    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return services != null && services.contains("MacroAccessibilityService");
        }
        return false;
    }

    private void showAccessibilityPrompt() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}

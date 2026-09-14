package com.example.macroapp.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.macroapp.model.MacroRecording;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * MacroStorageManager - Handle penyimpanan dan loading macro recordings
 * 
 * Menggunakan:
 * - SharedPreferences untuk metadata
 * - JSON untuk serialisasi gesture events
 */
public class MacroStorageManager {
    
    private static final String TAG = "MacroStorageManager";
    private static final String PREFS_NAME = "MacroAppPreferences";
    private static final String KEY_CURRENT_MACRO = "current_macro";
    private static final String KEY_LAST_RECORDING = "last_recording";
    
    private Context context;
    private SharedPreferences preferences;
    private Gson gson;
    
    public MacroStorageManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder().create();
        Log.d(TAG, "MacroStorageManager initialized");
    }
    
    /**
     * Save current macro recording
     * 
     * @param recording MacroRecording object to save
     * @return true if save successful
     */
    public boolean saveMacroRecording(MacroRecording recording) {
        if (recording == null) {
            Log.e(TAG, "Cannot save null recording");
            return false;
        }
        
        try {
            // Finalize recording sebelum save
            recording.finalize();
            
            // Serialize to JSON
            String json = gson.toJson(recording);
            
            // Save to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CURRENT_MACRO, json);
            editor.putLong(KEY_LAST_RECORDING, System.currentTimeMillis());
            boolean result = editor.commit();
            
            if (result) {
                Log.d(TAG, "✓ Macro saved successfully: " + recording.getSummary());
            } else {
                Log.w(TAG, "✗ Failed to commit macro save");
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error saving macro recording", e);
            return false;
        }
    }
    
    /**
     * Load current macro recording
     * 
     * @return MacroRecording if exists, null otherwise
     */
    public MacroRecording loadMacroRecording() {
        try {
            String json = preferences.getString(KEY_CURRENT_MACRO, null);
            
            if (json == null || json.isEmpty()) {
                Log.d(TAG, "No saved macro found");
                return null;
            }
            
            MacroRecording recording = gson.fromJson(json, MacroRecording.class);
            Log.d(TAG, "✓ Macro loaded successfully: " + recording.getSummary());
            return recording;
        } catch (Exception e) {
            Log.e(TAG, "Error loading macro recording", e);
            return null;
        }
    }
    
    /**
     * Check jika ada macro yang tersimpan
     * 
     * @return true if macro exists
     */
    public boolean hasSavedMacro() {
        String json = preferences.getString(KEY_CURRENT_MACRO, null);
        return json != null && !json.isEmpty();
    }
    
    /**
     * Delete current macro recording
     * 
     * @return true if delete successful
     */
    public boolean deleteMacroRecording() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(KEY_CURRENT_MACRO);
            editor.remove(KEY_LAST_RECORDING);
            boolean result = editor.commit();
            
            if (result) {
                Log.d(TAG, "✓ Macro deleted successfully");
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting macro recording", e);
            return false;
        }
    }
    
    /**
     * Get last recording timestamp
     * 
     * @return timestamp in milliseconds
     */
    public long getLastRecordingTime() {
        return preferences.getLong(KEY_LAST_RECORDING, 0);
    }
    
    /**
     * Clear all saved data
     */
    public void clearAll() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            Log.d(TAG, "✓ All data cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data", e);
        }
    }
}

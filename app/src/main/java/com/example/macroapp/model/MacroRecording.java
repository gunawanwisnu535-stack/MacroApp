package com.example.macroapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MacroRecording - Container untuk sekumpulan gesture events
 * 
 * Menyimpan:
 * - List of gesture events
 * - Metadata (name, created time, total duration)
 */
public class MacroRecording implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name; // Nama macro
    private String description; // Deskripsi
    private long createdAt; // Waktu pembuatan
    private long recordingDuration; // Total durasi recording
    private List<GestureEvent> gestures; // List gesture events
    private int gestureCount; // Jumlah gesture
    
    public MacroRecording() {
        this.gestures = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.gestureCount = 0;
    }
    
    public MacroRecording(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Add gesture ke recording
     */
    public void addGesture(GestureEvent gesture) {
        if (gesture != null) {
            if (!gestures.isEmpty()) {
                // Calculate delay dari gesture sebelumnya
                long lastTimestamp = gestures.get(gestures.size() - 1).getTimestamp();
                long delay = gesture.getTimestamp() - lastTimestamp;
                gesture.setDelay(delay);
            } else {
                gesture.setDelay(0);
            }
            gestures.add(gesture);
            gestureCount++;
        }
    }
    
    /**
     * Get gesture by index
     */
    public GestureEvent getGesture(int index) {
        if (index >= 0 && index < gestures.size()) {
            return gestures.get(index);
        }
        return null;
    }
    
    /**
     * Clear semua gestures
     */
    public void clear() {
        gestures.clear();
        gestureCount = 0;
        recordingDuration = 0;
    }
    
    /**
     * Finalize recording - calculate total duration
     */
    public void finalize() {
        if (!gestures.isEmpty()) {
            long firstTimestamp = gestures.get(0).getTimestamp();
            long lastTimestamp = gestures.get(gestures.size() - 1).getTimestamp();
            recordingDuration = lastTimestamp - firstTimestamp;
        }
    }
    
    /**
     * Get total delays (waktu tunggu antar gesture)
     */
    public long getTotalDelayTime() {
        long totalDelay = 0;
        for (GestureEvent gesture : gestures) {
            totalDelay += gesture.getDelay();
        }
        return totalDelay;
    }
    
    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public long getCreatedAt() { return createdAt; }
    
    public long getRecordingDuration() { return recordingDuration; }
    public void setRecordingDuration(long duration) { this.recordingDuration = duration; }
    
    public List<GestureEvent> getGestures() { return gestures; }
    public void setGestures(List<GestureEvent> gestures) { 
        this.gestures = gestures;
        this.gestureCount = gestures != null ? gestures.size() : 0;
    }
    
    public int getGestureCount() { return gestureCount; }
    
    /**
     * Get summary string untuk display
     */
    public String getSummary() {
        return String.format("%s (%d gestures, %.1f seconds)", 
            name != null ? name : "Untitled",
            gestureCount,
            recordingDuration / 1000.0);
    }
    
    @Override
    public String toString() {
        return "MacroRecording{" +
                "name='" + name + '\'' +
                ", gestureCount=" + gestureCount +
                ", recordingDuration=" + recordingDuration +
                ", createdAt=" + createdAt +
                '}';
    }
}

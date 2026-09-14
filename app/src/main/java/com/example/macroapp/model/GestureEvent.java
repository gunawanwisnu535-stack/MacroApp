package com.example.macroapp.model;

import java.io.Serializable;

/**
 * GestureEvent - Model untuk merepresentasikan satu gesture action
 * 
 * Tipe gesture:
 * - CLICK: Single tap
 * - SWIPE: Gesture linear
 * - LONG_PRESS: Touch & hold
 */
public class GestureEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final int TYPE_CLICK = 1;
    public static final int TYPE_SWIPE = 2;
    public static final int TYPE_LONG_PRESS = 3;
    
    private int type; // Tipe gesture
    private long timestamp; // Waktu gesture terjadi
    private long delay; // Delay dari gesture sebelumnya (milliseconds)
    private int x1; // Koordinat awal X
    private int y1; // Koordinat awal Y
    private int x2; // Koordinat akhir X (untuk swipe)
    private int y2; // Koordinat akhir Y (untuk swipe)
    private long duration; // Durasi gesture (untuk long press)
    
    public GestureEvent() {
    }
    
    // Constructor untuk CLICK
    public GestureEvent(int type, int x, int y, long duration) {
        this.type = type;
        this.x1 = x;
        this.y1 = y;
        this.duration = duration;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Constructor untuk SWIPE
    public GestureEvent(int x1, int y1, int x2, int y2, long duration) {
        this.type = TYPE_SWIPE;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.duration = duration;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters & Setters
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public long getDelay() { return delay; }
    public void setDelay(long delay) { this.delay = delay; }
    
    public int getX1() { return x1; }
    public void setX1(int x1) { this.x1 = x1; }
    
    public int getY1() { return y1; }
    public void setY1(int y1) { this.y1 = y1; }
    
    public int getX2() { return x2; }
    public void setX2(int x2) { this.x2 = x2; }
    
    public int getY2() { return y2; }
    public void setY2(int y2) { this.y2 = y2; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    /**
     * Get tipe gesture sebagai string
     */
    public String getTypeString() {
        switch (type) {
            case TYPE_CLICK:
                return "CLICK";
            case TYPE_SWIPE:
                return "SWIPE";
            case TYPE_LONG_PRESS:
                return "LONG_PRESS";
            default:
                return "UNKNOWN";
        }
    }
    
    @Override
    public String toString() {
        return "GestureEvent{" +
                "type=" + getTypeString() +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", duration=" + duration +
                ", delay=" + delay +
                '}';
    }
}

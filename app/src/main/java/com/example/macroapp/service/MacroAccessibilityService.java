package com.example.macroapp.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

public class MacroAccessibilityService extends AccessibilityService {

    private static final String TAG = "MacroAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Accessibility Event: " + event.getEventType());
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service Connected");
    }

    /**
     * Simulasi single tap pada koordinat tertentu
     */
    public void simulateClick(int x, int y, long duration) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription strokeDescription =
                new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(strokeDescription)
                .build();
        dispatchGesture(gestureDescription, null, null);
        Log.d(TAG, "Click at " + x + ", " + y);
    }

    /**
     * Simulasi swipe dari (x1, y1) ke (x2, y2)
     */
    public void simulateSwipe(int x1, int y1, int x2, int y2, long duration) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        GestureDescription.StrokeDescription strokeDescription =
                new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(strokeDescription)
                .build();
        dispatchGesture(gestureDescription, null, null);
        Log.d(TAG, "Swipe from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ")");
    }

    /**
     * Simulasi long press
     */
    public void simulateLongPress(int x, int y, long duration) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription strokeDescription =
                new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gestureDescription = new GestureDescription.Builder()
                .addStroke(strokeDescription)
                .build();
        dispatchGesture(gestureDescription, null, null);
        Log.d(TAG, "Long press at " + x + ", " + y + " for " + duration + "ms");
    }
}

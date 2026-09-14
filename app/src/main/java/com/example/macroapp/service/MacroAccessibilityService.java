package com.example.macroapp.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

/**
 * MacroAccessibilityService - Accessibility Service untuk merekam dan memainkan macro gesture
 * 
 * Service ini menghandle:
 * - Simulasi tap (single touch)
 * - Simulasi swipe (gesture linear)
 * - Simulasi long press (touch & hold)
 * 
 * Requirements:
 * - Android 5.0+ (API 24+)
 * - Accessibility Service harus di-enable di Settings
 * - Tidak memerlukan root access
 */
public class MacroAccessibilityService extends AccessibilityService {

    private static final String TAG = "MacroAccessibilityService";
    private static final int MIN_LONG_PRESS_DURATION = 500; // milliseconds
    private static final int MAX_GESTURE_DURATION = 10000; // 10 seconds

    // Callback untuk hasil gesture execution
    private GestureResultCallback gestureCallback;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Log untuk tracking event yang terjadi di sistem
        if (event != null) {
            logAccessibilityEvent(event);
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service Connected");
        initGestureCallback();
    }

    /**
     * Initialize callback untuk hasil gesture
     */
    private void initGestureCallback() {
        gestureCallback = new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.d(TAG, "✓ Gesture completed successfully");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                Log.w(TAG, "✗ Gesture cancelled or interrupted");
            }
        };
    }

    /**
     * Simulasi single tap pada koordinat tertentu
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param duration Duration of touch (biasanya 50-100ms untuk tap normal)
     * @return true jika gesture berhasil di-dispatch, false jika parameter invalid
     */
    public boolean simulateClick(int x, int y, long duration) {
        // Validasi parameter
        if (!validateCoordinates(x, y)) {
            Log.e(TAG, "Invalid coordinates: x=" + x + ", y=" + y);
            return false;
        }
        if (!validateDuration(duration)) {
            Log.e(TAG, "Invalid duration: " + duration);
            return false;
        }

        try {
            Path path = new Path();
            path.moveTo(x, y);
            
            GestureDescription.StrokeDescription strokeDescription =
                    new GestureDescription.StrokeDescription(path, 0, duration);
            
            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(strokeDescription)
                    .build();
            
            boolean result = dispatchGesture(gestureDescription, gestureCallback, null);
            
            if (result) {
                Log.d(TAG, "✓ Click dispatched at (" + x + ", " + y + ") duration: " + duration + "ms");
            } else {
                Log.w(TAG, "✗ Failed to dispatch click at (" + x + ", " + y + ")");
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error executing click gesture", e);
            return false;
        }
    }

    /**
     * Simulasi swipe dari (x1, y1) ke (x2, y2)
     * 
     * @param x1 Starting X-coordinate
     * @param y1 Starting Y-coordinate
     * @param x2 Ending X-coordinate
     * @param y2 Ending Y-coordinate
     * @param duration Duration of swipe gesture (biasanya 300-500ms untuk swipe smooth)
     * @return true jika gesture berhasil di-dispatch, false jika parameter invalid
     */
    public boolean simulateSwipe(int x1, int y1, int x2, int y2, long duration) {
        // Validasi parameter
        if (!validateCoordinates(x1, y1) || !validateCoordinates(x2, y2)) {
            Log.e(TAG, "Invalid coordinates: (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")");
            return false;
        }
        if (!validateDuration(duration)) {
            Log.e(TAG, "Invalid duration: " + duration);
            return false;
        }

        // Warn jika swipe terlalu cepat (< 100ms)
        if (duration < 100) {
            Log.w(TAG, "Swipe duration very short (" + duration + "ms), hasil mungkin tidak smooth");
        }

        try {
            Path path = new Path();
            path.moveTo(x1, y1);
            path.lineTo(x2, y2);
            
            GestureDescription.StrokeDescription strokeDescription =
                    new GestureDescription.StrokeDescription(path, 0, duration);
            
            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(strokeDescription)
                    .build();
            
            boolean result = dispatchGesture(gestureDescription, gestureCallback, null);
            
            if (result) {
                double distance = Math.sqrt(
                    Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)
                );
                Log.d(TAG, "✓ Swipe dispatched from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 
                    + ") distance: " + String.format("%.1f", distance) + "px duration: " + duration + "ms");
            } else {
                Log.w(TAG, "✗ Failed to dispatch swipe");
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error executing swipe gesture", e);
            return false;
        }
    }

    /**
     * Simulasi long press (touch & hold) pada koordinat tertentu
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param duration Duration of press (minimum 500ms, recommended 500-2000ms)
     * @return true jika gesture berhasil di-dispatch, false jika parameter invalid
     */
    public boolean simulateLongPress(int x, int y, long duration) {
        // Validasi parameter
        if (!validateCoordinates(x, y)) {
            Log.e(TAG, "Invalid coordinates: x=" + x + ", y=" + y);
            return false;
        }
        if (!validateDuration(duration)) {
            Log.e(TAG, "Invalid duration: " + duration);
            return false;
        }

        // Enforce minimum duration untuk long press
        if (duration < MIN_LONG_PRESS_DURATION) {
            Log.w(TAG, "Long press duration too short (" + duration + "ms), enforcing minimum " + MIN_LONG_PRESS_DURATION + "ms");
            duration = MIN_LONG_PRESS_DURATION;
        }

        try {
            Path path = new Path();
            path.moveTo(x, y);
            
            GestureDescription.StrokeDescription strokeDescription =
                    new GestureDescription.StrokeDescription(path, 0, duration);
            
            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(strokeDescription)
                    .build();
            
            boolean result = dispatchGesture(gestureDescription, gestureCallback, null);
            
            if (result) {
                Log.d(TAG, "✓ Long press dispatched at (" + x + ", " + y + ") duration: " + duration + "ms");
            } else {
                Log.w(TAG, "✗ Failed to dispatch long press at (" + x + ", " + y + ")");
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error executing long press gesture", e);
            return false;
        }
    }

    /**
     * Simulasi multi-touch (future enhancement)
     * Note: Current implementation supports single stroke only
     * 
     * @param x1 First touch X-coordinate
     * @param y1 First touch Y-coordinate
     * @param x2 Second touch X-coordinate
     * @param y2 Second touch Y-coordinate
     * @param duration Duration of touch
     * @return true jika gesture berhasil di-dispatch
     */
    public boolean simulateTwoFingerTap(int x1, int y1, int x2, int y2, long duration) {
        if (!validateCoordinates(x1, y1) || !validateCoordinates(x2, y2)) {
            Log.e(TAG, "Invalid coordinates for two-finger tap");
            return false;
        }
        if (!validateDuration(duration)) {
            Log.e(TAG, "Invalid duration: " + duration);
            return false;
        }

        try {
            Path path1 = new Path();
            path1.moveTo(x1, y1);
            
            Path path2 = new Path();
            path2.moveTo(x2, y2);
            
            GestureDescription.StrokeDescription stroke1 =
                    new GestureDescription.StrokeDescription(path1, 0, duration);
            GestureDescription.StrokeDescription stroke2 =
                    new GestureDescription.StrokeDescription(path2, 0, duration);
            
            GestureDescription gestureDescription = new GestureDescription.Builder()
                    .addStroke(stroke1)
                    .addStroke(stroke2)
                    .build();
            
            boolean result = dispatchGesture(gestureDescription, gestureCallback, null);
            
            if (result) {
                Log.d(TAG, "✓ Two-finger tap dispatched at (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ")");
            } else {
                Log.w(TAG, "✗ Failed to dispatch two-finger tap");
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error executing two-finger tap gesture", e);
            return false;
        }
    }

    /**
     * Validasi koordinat (X, Y harus >= 0)
     */
    private boolean validateCoordinates(int x, int y) {
        return x >= 0 && y >= 0;
    }

    /**
     * Validasi duration (harus > 0 dan <= MAX_GESTURE_DURATION)
     */
    private boolean validateDuration(long duration) {
        return duration > 0 && duration <= MAX_GESTURE_DURATION;
    }

    /**
     * Log accessibility events untuk debugging dan tracking
     */
    private void logAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG, "Window changed: " + event.getClassName() + " - " + event.getPackageName());
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.d(TAG, "View clicked: " + event.getClassName() + " - " + event.getPackageName());
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                Log.d(TAG, "View long clicked: " + event.getClassName());
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Log.d(TAG, "Text changed: " + event.getText());
                break;
            // Add more event types as needed
        }
    }

    /**
     * Get current accessibility service status
     */
    public boolean isServiceEnabled() {
        return true;
    }

    /**
     * Cleanup resources when service is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Accessibility Service Destroyed");
        gestureCallback = null;
    }
}

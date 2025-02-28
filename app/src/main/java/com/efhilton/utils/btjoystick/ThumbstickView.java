package com.efhilton.utils.btjoystick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.jvm.functions.Function2;

public class ThumbstickView extends FrameLayout {
    AtomicInteger joystickIndex = new AtomicInteger(-1);
    public Function2<Float, Float, Void> onMoveCallback;
    private TextView xLabel;
    private TextView yLabel;
    private ImageView joystick;
    private ImageView joystickBg;
    private int counter = 0;

    public ThumbstickView(Context context) {
        super(context);
        init(null, 0);
    }

    public ThumbstickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ThumbstickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        onMoveCallback = (x, y) -> {
            System.out.println("Joystick: X = " + x + ", Y = " + y);
            return null;
        };

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.thumbstick_view, this, true);

        // Get references to the ImageViews
        joystick = findViewById(R.id.thumbstick);
        joystickBg = findViewById(R.id.thumbstick_bg);
        xLabel = findViewById(R.id.x_values);
        yLabel = findViewById(R.id.y_values);

        setupJoystick();
    }

    private boolean onTouchListener(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (joystickIndex.get() == -1) {
                    final int pointerIndex = event.getActionIndex();
                    joystickIndex.set(pointerIndex);
                }
//                centerThumbstick(joystick, onMoveCallback, joystickBg);
                break;
            case MotionEvent.ACTION_MOVE: {
                trackThumbMovements(joystick, onMoveCallback, event, joystickIndex.get(), joystickBg);
                break;
            }
            case MotionEvent.ACTION_UP:
                centerThumbstick(joystick, onMoveCallback, joystickBg);
                break;
            case MotionEvent.ACTION_CANCEL: {
                centerThumbstick(joystick, onMoveCallback, joystickBg);
                break;
            }
            default:
                System.out.println("Default triggered with action: " + event.getActionMasked() + " (" + event.getAction() + ") and index: " + event.getActionIndex());
                break;
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupJoystick() {
        joystick.setOnClickListener(null);
        joystick.setOnTouchListener(ThumbstickView.this::onTouchListener);
    }

    private void trackThumbMovements(ImageView joystick, Function2<Float, Float, Void> onMove, MotionEvent event,
                                     int pointerIndex, ImageView bg) {
        final float rawEventX = event.getRawX(pointerIndex);
        final float rawEventY = event.getRawY(pointerIndex);

        int[] bgLocation = new int[2];
        bg.getLocationOnScreen(bgLocation);
        final float bgCenterX = bgLocation[0] + bg.getWidth() / 2f; // Center on the screen
        final float bgCenterY = bgLocation[1] + bg.getHeight() / 2f; // Center on the screen
        final float bgRadius = bg.getWidth() / 2f;
        final float limitRadius = bgRadius - joystick.getWidth() / 2f;

        // Calculate the difference between the touch point and the center (in screen coordinates)
        final float dx = rawEventX - bgCenterX;
        final float dy = rawEventY - bgCenterY;
        final float eventDistance = (float) Math.sqrt(dx * dx + dy * dy);
        final float eventAngle = (float) Math.atan2(dy, dx);

        final float limitedDistance = Math.min(eventDistance, limitRadius);
        final float newX = bgCenterX + limitedDistance * (float) Math.cos(eventAngle); // Screen coordinates
        final float newY = bgCenterY + limitedDistance * (float) Math.sin(eventAngle); // Screen coordinates

        // Convert the new screen coordinates to view-relative coordinates for the joystick
        int[] joystickParentLocation = new int[2];
        ((View) joystick.getParent()).getLocationOnScreen(joystickParentLocation);
        final float joystickNewX = newX - joystickParentLocation[0] - joystick.getWidth() / 2f;
        final float joystickNewY = newY - joystickParentLocation[1] - joystick.getHeight() / 2f;

        joystick.setX(joystickNewX);
        joystick.setY(joystickNewY);

        float normalizedX = (newX - bgCenterX) / limitRadius;
        float normalizedY = -(newY - bgCenterY) / limitRadius;

        // the lower, the more aggressive.
        int DOWN_SAMPLE = 2;
        counter = (counter + 1) % DOWN_SAMPLE;
        if (counter == 0) {
            updateDisplayValues(normalizedX, normalizedY, onMove);
        }
    }

    private void centerThumbstick(ImageView joystick, Function2<Float, Float, Void> onMove, ImageView bg) {
        float centerX = bg.getX() + bg.getWidth() / 2f;
        float centerY = bg.getY() + bg.getHeight() / 2f;
        joystick.animate()
                .x(centerX - joystick.getWidth() / 2f)
                .y(centerY - joystick.getHeight() / 2f)
                .setDuration(100)
                .start();

        updateDisplayValues(0, 0, onMove);
    }

    private void updateDisplayValues(float x, float y, Function2<Float, Float, Void> onMove) {
        xLabel.setText(String.format(Locale.ENGLISH, "X=%+1.2f", x));
        yLabel.setText(String.format(Locale.ENGLISH, "Y=%+1.2f", y));
        if (onMove != null) {
            onMove.invoke(x, y);
        }
    }
}
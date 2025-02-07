package com.efhilton.utils.btjoystick;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsoleOutputView extends ScrollView {
    TextView textView;
    ScrollView scrollView;

    public ConsoleOutputView(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public ConsoleOutputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ConsoleOutputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public ConsoleOutputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.console_output_view, this, true);

        textView = findViewById(R.id.console_text);
        scrollView = findViewById(R.id.console_output_view);

        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void setText(String text) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedText = now.format(formatter) + ": " + text.replace("\n", "") + "\n";

        textView.post(() -> {
            textView.append(formattedText);
            scrollToBottom();
        });
    }
    private void scrollToBottom() {
        final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
        if (scrollAmount > 0) {
            textView.scrollTo(0, scrollAmount);
        }
    }
    public void setText(int id) {
        setText(getResources().getString(id));
    }
}
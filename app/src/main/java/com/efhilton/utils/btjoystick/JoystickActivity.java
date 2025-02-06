package com.efhilton.utils.btjoystick;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.efhilton.utils.btjoystick.databinding.ActivityJoystickBinding;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class JoystickActivity extends AppCompatActivity {
    // TODO: add Bluetooth Support for output commands
    // TODO: add Bluetooth Support for incoming text
    // TODO: Add connect/disconnect button?
    // TODO: Create text console output view. This should act independently and load data off a protected ringbuffer.
    // TODO: Create settings activity.
    private ThumbstickView thumbstickLeftStick;
    private ThumbstickView thumbstickRightStick;
    private TextView outputConsole;
    AtomicBoolean isConnected;
    private ActivityJoystickBinding binding;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJoystickBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        outputConsole = binding.outputConsole;

        View.OnClickListener switchToggled = (v) -> {
            CheckBox switchCompat = (CheckBox) v;
            int id = v.getId();
            boolean isChecked = switchCompat.isChecked();
            int nameId;

            if (id == R.id.switch00) {
                nameId = R.string.f00;
            } else if (id == R.id.switch01) {
                nameId = R.string.f01;
            } else if (id == R.id.switch02) {
                nameId = R.string.f02;
            } else if (id == R.id.switch03) {
                nameId = R.string.f03;
            } else if (id == R.id.switch04) {
                nameId = R.string.f04;
            } else if (id == R.id.switch05) {
                nameId = R.string.f05;
            } else if (id == R.id.switch06) {
                nameId = R.string.f06;
            } else if (id == R.id.switch07) {
                nameId = R.string.f07;
            } else if (id == R.id.switch08) {
                nameId = R.string.f08;
            } else if (id == R.id.switch09) {
                nameId = R.string.f09;
            } else if (id == R.id.switch10) {
                nameId = R.string.f10;
            } else if (id == R.id.switch11) {
                nameId = R.string.f11;
            } else if (id == R.id.switch12) {
                nameId = R.string.f12;
            } else if (id == R.id.switch13) {
                nameId = R.string.f13;
            } else if (id == R.id.switch14) {
                nameId = R.string.f14;
            } else if (id == R.id.switch15) {
                nameId = R.string.f15;
            } else {
                return;
            }

            String name = getString(nameId);
            printSwitchStatusChange(name, isChecked);
        };
        binding.switch00.setOnClickListener(switchToggled);
        binding.switch01.setOnClickListener(switchToggled);
        binding.switch02.setOnClickListener(switchToggled);
        binding.switch03.setOnClickListener(switchToggled);
        binding.switch04.setOnClickListener(switchToggled);
        binding.switch05.setOnClickListener(switchToggled);
        binding.switch06.setOnClickListener(switchToggled);
        binding.switch07.setOnClickListener(switchToggled);
        binding.switch08.setOnClickListener(switchToggled);
        binding.switch09.setOnClickListener(switchToggled);
        binding.switch10.setOnClickListener(switchToggled);
        binding.switch11.setOnClickListener(switchToggled);
        binding.switch12.setOnClickListener(switchToggled);
        binding.switch13.setOnClickListener(switchToggled);
        binding.switch14.setOnClickListener(switchToggled);
        binding.switch15.setOnClickListener(switchToggled);

        View.OnClickListener buttonPressed = v -> {
            int nameId;
            if (v.getId() == R.id.button00) {
                nameId = R.string.b00;
            } else if (v.getId() == R.id.button01) {
                nameId = R.string.b01;
            } else if (v.getId() == R.id.button02) {
                nameId = R.string.b02;
            } else if (v.getId() == R.id.button03) {
                nameId = R.string.b03;
            } else if (v.getId() == R.id.button04) {
                nameId = R.string.b04;
            } else if (v.getId() == R.id.button05) {
                nameId = R.string.b05;
            } else if (v.getId() == R.id.button06) {
                nameId = R.string.b06;
            } else if (v.getId() == R.id.button07) {
                nameId = R.string.b07;
            } else if (v.getId() == R.id.button08) {
                nameId = R.string.b08;
            } else if (v.getId() == R.id.button09) {
                nameId = R.string.b09;
            } else if (v.getId() == R.id.button10) {
                nameId = R.string.b10;
            } else if (v.getId() == R.id.button11) {
                nameId = R.string.b11;
            } else if (v.getId() == R.id.button12) {
                nameId = R.string.b12;
            } else if (v.getId() == R.id.button13) {
                nameId = R.string.b13;
            } else if (v.getId() == R.id.button14) {
                nameId = R.string.b14;
            } else if (v.getId() == R.id.button15) {
                nameId = R.string.b15;
            } else {
                return;
            }
            String name = getString(nameId);
            String text = String.format(Locale.ENGLISH, "'" + name + "' was triggered");
            outputConsole.setText(text);
        };
        binding.button00.setOnClickListener(buttonPressed);
        binding.button01.setOnClickListener(buttonPressed);
        binding.button02.setOnClickListener(buttonPressed);
        binding.button03.setOnClickListener(buttonPressed);
        binding.button04.setOnClickListener(buttonPressed);
        binding.button05.setOnClickListener(buttonPressed);
        binding.button06.setOnClickListener(buttonPressed);
        binding.button07.setOnClickListener(buttonPressed);
        binding.button08.setOnClickListener(buttonPressed);
        binding.button09.setOnClickListener(buttonPressed);
        binding.button10.setOnClickListener(buttonPressed);
        binding.button11.setOnClickListener(buttonPressed);
        binding.button12.setOnClickListener(buttonPressed);
        binding.button13.setOnClickListener(buttonPressed);
        binding.button14.setOnClickListener(buttonPressed);
        binding.button15.setOnClickListener(buttonPressed);

        thumbstickLeftStick = binding.thumbstickLeft;
        thumbstickLeftStick.onMoveCallback = (x, y) -> {
            String text = String.format(Locale.ENGLISH, "LEFT: (%+1.02f, %+1.02f)\n", x, y);
            outputConsole.setText(text);
            return null;
        };
        thumbstickRightStick = binding.thumbstickRight;
        thumbstickRightStick.onMoveCallback = (x, y) -> {
            String text = String.format(Locale.ENGLISH, "RIGHT: (%+1.02f, %+1.02f)\n", x, y);
            outputConsole.setText(text);
            return null;
        };
        View.OnClickListener consoleClicked = v -> {
            outputConsole.setText("Boop! Opening Settings...");
        };

        outputConsole.setText(R.string.click_for_settings);
        outputConsole.setOnClickListener(consoleClicked);

        isConnected = new AtomicBoolean(false);
        ImageView connectButton = binding.connectButton;
        connectButton.setOnClickListener(v -> {
                    if (isConnected.get()){
                        connectButton.setImageResource(R.drawable.ic_not_connected);
                        isConnected.set(false);
                    } else {
                        connectButton.setImageResource(R.drawable.ic_connected);
                        isConnected.set(true);
                    }
                }
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void printSwitchStatusChange(String fcnName, boolean isChecked) {
        String text = String.format(Locale.ENGLISH, "Function '%s' is %s", fcnName, isChecked ? "ON" : "OFF");
        outputConsole.setText(text);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
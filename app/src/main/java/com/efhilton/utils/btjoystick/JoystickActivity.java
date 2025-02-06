package com.efhilton.utils.btjoystick;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.efhilton.utils.btjoystick.databinding.ActivityJoystickBinding;

public class JoystickActivity extends AppCompatActivity {
    private JoystickView thumbstickLeftStick;
    private JoystickView thumbstickRightStick;

    private ActivityJoystickBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJoystickBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            } else {
                return;
            }
            String name = getString(nameId);
            System.out.println("Button " + name + " pressed");
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

        thumbstickLeftStick = binding.thumbstickLeft;
        thumbstickLeftStick.onMoveCallback = (x, y) -> {
            System.out.println("LEFT Joystick: X = " + x + ", Y = " + y);
            return null;
        };
        thumbstickRightStick = binding.thumbstickRight;
        thumbstickRightStick.onMoveCallback = (x, y) -> {
            System.out.println("RIGHT Joystick: X = " + x + ", Y = " + y);
            return null;
        };
    }

    private void printSwitchStatusChange(String fcnName, boolean isChecked) {
        System.out.println("Function " + fcnName + " is " + (isChecked ? "ON" : "OFF"));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
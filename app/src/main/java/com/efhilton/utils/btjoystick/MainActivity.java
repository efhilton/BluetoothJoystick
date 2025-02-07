package com.efhilton.utils.btjoystick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import android.content.SharedPreferences;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.efhilton.utils.btjoystick.databinding.MainActivityBinding;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // TODO: add Bluetooth Support for output commands
    // TODO: add Bluetooth Support for incoming text
    private ThumbstickView thumbstickLeftStick;
    private ThumbstickView thumbstickRightStick;
    private ImageView connectButton;
    private ConsoleOutputView outputConsole;
    private MainActivityBinding binding;
    private UUID serviceUid;
    private UUID characteristicUid;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic characteristic;


    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
        bluetoothGatt = null;
        characteristic = null;
        binding = null;
        thumbstickLeftStick = null;
        thumbstickRightStick = null;
        outputConsole = null;

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        outputConsole = binding.consoleOutput;

        binding.switch00.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch01.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch02.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch03.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch04.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch05.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch06.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch07.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch08.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch09.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch10.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch11.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch12.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch13.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch14.setOnClickListener(MainActivity.this::switchToggled);
        binding.switch15.setOnClickListener(MainActivity.this::switchToggled);

        binding.button00.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button01.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button02.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button03.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button04.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button05.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button06.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button07.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button08.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button09.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button10.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button11.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button12.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button13.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button14.setOnClickListener(MainActivity.this::buttonPressed);
        binding.button15.setOnClickListener(MainActivity.this::buttonPressed);

        thumbstickLeftStick = binding.thumbstickLeft;
        thumbstickLeftStick.onMoveCallback = (x, y) -> {
            String text = String.format(Locale.ENGLISH, "LEFT: (%+1.02f, %+1.02f)", x, y);
            outputConsole.setText(text);
            return null;
        };
        thumbstickRightStick = binding.thumbstickRight;
        thumbstickRightStick.onMoveCallback = (x, y) -> {
            String text = String.format(Locale.ENGLISH, "RIGHT: (%+1.02f, %+1.02f)", x, y);
            outputConsole.setText(text);
            return null;
        };

        outputConsole.clear();

        ImageView settingsButton = binding.settingsButton;
        settingsButton.setOnClickListener(v -> {
                    outputConsole.setText(R.string.opening_settings_screen);
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
        );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = btManager.getAdapter();
        final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    connectButton.setImageResource(R.drawable.ic_connected);
                    outputConsole.setText("Connected");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connectButton.setImageResource(R.drawable.ic_not_connected);
                    outputConsole.setText("Disconnected");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    final BluetoothGattService service = gatt.getService(serviceUid);
                    if (service == null) {
                        showProtectedMessage("Unable to Find Service. Please check your Service and Characteristic UUIDs");
                    } else {
                        characteristic = service.getCharacteristic(characteristicUid); // Initialize here
                        if (characteristic == null) {
                            showProtectedMessage("Unable to Find Characteristic. Please check your Service and Characteristic UUIDs");
                        }
                    }
                }
                enableButtons(characteristic != null);
            }
        };

        enableButtons(false);

        connectButton = binding.connectButton;
        connectButton.setOnClickListener(v -> {
                    if (!v.isActivated()) {
                        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        final String serviceUidString = sharedPreferences.getString("service-uid", "0000180f-0000-1000-8000-00805f9b34fb");
                        final String characteristicUidString = sharedPreferences.getString("characteristic-uid", "00002a19-0000-1000-8000-00805f9b34fb");
                        final String macAddress = sharedPreferences.getString("mac-address", "00:00:00:00:00:00");
                        this.serviceUid = UUID.fromString(serviceUidString);
                        this.characteristicUid = UUID.fromString(characteristicUidString);

                        outputConsole.setText("Connecting to " + macAddress + "...");
                        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                        bluetoothGatt = device.connectGatt(this, true, gattCallback);
                    } else {
                        outputConsole.setText("Disconnecting...");
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                        characteristic = null;
                        enableButtons(false);
                    }
                }
        );
    }

    private void buttonPressed(View v) {
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
    }

    void switchToggled(View v) {
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
    }

    private void enableButtons(boolean b) {
        System.out.printf("Enabling buttons: %s", b ? "true" : "false");
    }

    private void showProtectedMessage(String s) {
        System.out.println(s);
    }

    private void printSwitchStatusChange(String fcnName, boolean isChecked) {
        String text = String.format(Locale.ENGLISH, "Function '%s' is %s", fcnName, isChecked ? "ON" : "OFF");
        outputConsole.setText(text);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private final ActivityResultLauncher<String> requestBluetoothPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Log.d("MainActivity", "BLUETOOTH_CONNECT permission granted");
                    // Proceed with Bluetooth operations here
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    Log.d("MainActivity", "BLUETOOTH_CONNECT permission denied");
                }
            });

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
            // You have the permission. Proceed with Bluetooth operations.
            Log.d("MainActivity", "BLUETOOTH_CONNECT permission already granted");
        } else {
            // You don't have the permission. Request it.
            requestBluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothPermission();
    }
}
package com.efhilton.utils.btjoystick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ThumbstickView thumbstickLeftStick;
    private ThumbstickView thumbstickRightStick;
    private ImageView connectButton;
    private ConsoleOutputView outputConsole;
    private MainActivityBinding binding;
    private BluetoothService bluetoothService;
    private boolean isServiceBound = false;

    // Define the ServiceConnection
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isServiceBound = true;
            outputConsole.setText("Ready to connect.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
            isServiceBound = false;
            outputConsole.setText("Service is shutdown.");
        }
    };
    private final BroadcastReceiver connectionStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Received broadcast: " + intent.getAction());
            if (BluetoothService.ACTION_CONNECTION_STATUS.equals(intent.getAction())) {
                boolean isConnected = intent.getBooleanExtra(BluetoothService.EXTRA_IS_CONNECTED, false);
                if (isConnected) {
                    outputConsole.setText("Device Connected");
                    connectButton.setImageResource(R.drawable.ic_connected);
                    connectButton.setActivated(true);
                } else {
                    outputConsole.setText("Device Disconnected");
                    connectButton.setImageResource(R.drawable.ic_not_connected);
                    connectButton.setActivated(false);
                }
            }
        }
    };

    private final BroadcastReceiver receivedDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Received broadcast: " + intent.getAction());
            if (BluetoothService.ACTION_RECEIVED_DATA.equals(intent.getAction())) {
                final String receivedText = intent.getStringExtra(BluetoothService.EXTRA_RECEIVED_TEXT);
                outputConsole.setText(receivedText);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        thumbstickLeftStick = null;
        thumbstickRightStick = null;
        outputConsole = null;

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void startBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Intent startIntent = new Intent(this, BluetoothService.class);
        startIntent.setAction(BluetoothService.ACTION_START_FOREGROUND_SERVICE);
        startService(startIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart: Registering receivers");
        registerReceiver(connectionStatusReceiver, new IntentFilter(BluetoothService.ACTION_CONNECTION_STATUS), Context.RECEIVER_NOT_EXPORTED);
        registerReceiver(receivedDataReceiver, new IntentFilter(BluetoothService.ACTION_RECEIVED_DATA), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop: Unregistering receivers");
        unregisterReceiver(connectionStatusReceiver);
        unregisterReceiver(receivedDataReceiver);
        stopBluetoothService();
    }

    private void stopBluetoothService() {
        Intent stopIntent = new Intent(this, BluetoothService.class);
        stopIntent.setAction(BluetoothService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(stopIntent);
    }

    @SuppressLint({"MissingPermission", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        outputConsole = binding.consoleOutput;

        startBluetoothService();

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
            sendJoystickValues('L', x, y);
            return null;
        };
        thumbstickRightStick = binding.thumbstickRight;
        thumbstickRightStick.onMoveCallback = (x, y) -> {
            sendJoystickValues('R', x, y);
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

        connectButton = binding.connectButton;
        connectButton.setOnClickListener(v -> {
                    if (!v.isActivated()) {
                        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        final String macAddress = sharedPreferences.getString("mac-address", "F4:12:FA:67:3E:92");
                        final UUID serviceUidString = UUID.fromString(sharedPreferences.getString("service-uid", "f0125634-1278-5634-1278-5634127856f0"));
                        final UUID characteristicUidString = UUID.fromString(sharedPreferences.getString("characteristic-uid", "abcdef12-3456-7890-ab12-cdef56789012"));
                        outputConsole.setText("Connecting to '" + macAddress.toUpperCase() + "'...");
                        connectToBluetoothDevice(macAddress, serviceUidString, characteristicUidString);
                    } else {
                        outputConsole.setText("Disconnecting...");
                        disconnectFromBluetoothDevice();
                    }
                }
        );

    }

    private void connectToBluetoothDevice(String macAddress, UUID serviceUidString, UUID characteristicUidString) {
        if (isServiceBound && bluetoothService != null) {
            Intent intent = new Intent(this, BluetoothService.class);
            intent.putExtra(BluetoothService.EXTRA_MAC_ADDRESS, macAddress);
            intent.putExtra(BluetoothService.EXTRA_SERVICE_UID, serviceUidString.toString());
            intent.putExtra(BluetoothService.EXTRA_CHARACTERISTIC_UID, characteristicUidString.toString());
            intent.setAction(BluetoothService.ACTION_CONNECT_TO_DEVICE);
            startService(intent);
        }
    }

    private void disconnectFromBluetoothDevice() {
        if (isServiceBound && bluetoothService != null) {
            Intent intent = new Intent(this, BluetoothService.class);
            intent.setAction(BluetoothService.ACTION_DISCONNECT);
            startService(intent);
        }
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
        sendTrigger(name);
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
        sendFunctionToggle(name, isChecked);
    }


    private void sendJoystickValues(char stick, float x, float y) {
        short xShort = (short) (x * 32767);
        short yShort = (short) (y * 32767);
        sendData(new byte[]{(byte) stick, (byte) (xShort >> 8), (byte) (xShort), (byte) (yShort >> 8), (byte) (yShort)});
    }

    private void sendFunctionToggle(String fcn, boolean isChecked) {
        char function = fcn.charAt(0);
        char idCharHex = fcn.charAt(1);
        // Convert idCharHex to integer
        int id = (idCharHex <= '9') ? idCharHex - '0' : idCharHex - 'A' + 10;
        sendData(new byte[]{(byte) function, (byte) id, (byte) (isChecked ? 1 : 0)});
    }

    private void sendTrigger(String fcn) {
        char function = fcn.charAt(0);
        char idCharHex = fcn.charAt(1);
        // Convert idCharHex to integer
        int id = (idCharHex <= '9') ? idCharHex - '0' : idCharHex - 'A' + 10;
        sendData(new byte[]{(byte) function, (byte) id});
    }

    private void sendData(byte[] data) {
        Intent intent = new Intent(this, BluetoothService.class);
        intent.putExtra(BluetoothService.EXTRA_DATA, data);
        intent.setAction(BluetoothService.ACTION_SEND_DATA);
        startService(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private final ActivityResultLauncher<String[]> requestBluetoothPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    // All permissions are granted. Continue with Bluetooth operations.
                    Log.d("MainActivity", "All Bluetooth permissions granted");
                    // Proceed with Bluetooth operations here
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    Log.d("MainActivity", "Some Bluetooth permissions denied");
                }
            });

    private void checkBluetoothPermission() {

        List<String> permissionsToRequest = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE);
        }

        // Request permissions if needed
        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            requestBluetoothPermissionsLauncher.launch(permissionsArray);
        } else {
            // All permissions are already granted
            Log.d("MainActivity", "All Bluetooth permissions already granted");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothPermission();
    }
}
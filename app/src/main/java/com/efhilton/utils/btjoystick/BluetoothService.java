package com.efhilton.utils.btjoystick;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService service;
    private UUID serviceUid;
    private UUID characteristicUid;
    private String macAddress;

    private static final String CHANNEL_ID = "com.efhilton.utils.btjoystick.BluetoothServiceChannel";
    private static final String NOTIFICATION_CHANNEL = "com.efhilton.utils.btjoystick.NOTIFICATION_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    public static final String ACTION_CONNECTION_STATUS = "com.efhilton.utils.btjoystick.ACTION_CONNECTION_STATUS";
    public static final String ACTION_CONNECT_TO_DEVICE = "com.efhilton.utils.btjoystick.ACTION_CONNECT_TO_DEVICE";
    public static final String ACTION_DISCONNECT = "com.efhilton.utils.btjoystick.ACTION_DISCONNECT";
    public static final String ACTION_RECEIVED_DATA = "com.efhilton.utils.btjoystick.ACTION_RECEIVED_DATA";
    public static final String ACTION_SEND_DATA = "com.efhilton.utils.btjoystick.ACTION_SEND_DATA";
    public static final String ACTION_START_FOREGROUND_SERVICE = "com.efhilton.utils.btjoystick.ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "com.efhilton.utils.btjoystick.ACTION_STOP_FOREGROUND_SERVICE";
    public static final String EXTRA_CHARACTERISTIC_UID = "com.efhilton.utils.btjoystick.EXTRA_CHARACTERISTIC_UID";
    public static final String EXTRA_DATA = "com.efhilton.utils.btjoystick.EXTRA_DATA";
    public static final String EXTRA_IS_CONNECTED = "com.efhilton.utils.btjoystick.ACTION_IS_CONNECTED_STATUS";
    public static final String EXTRA_MAC_ADDRESS = "com.efhilton.utils.btjoystick.EXTRA_MAC_ADDRESS";
    public static final String EXTRA_RECEIVED_TEXT = "com.efhilton.utils.btjoystick.EXTRA_RECEIVED_TEXT";
    public static final String EXTRA_SERVICE_UID = "com.efhilton.utils.btjoystick.EXTRA_SERVICE_UID";

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE:
                        startForegroundService();
                        break;
                    case ACTION_STOP_FOREGROUND_SERVICE:
                        stopForegroundService();
                        break;
                    case ACTION_CONNECT_TO_DEVICE:
                        macAddress = intent.getStringExtra(EXTRA_MAC_ADDRESS);
                        serviceUid = UUID.fromString(intent.getStringExtra(EXTRA_SERVICE_UID));
                        characteristicUid = UUID.fromString(intent.getStringExtra(EXTRA_CHARACTERISTIC_UID));
                        connectToDevice();
                        break;
                    case ACTION_SEND_DATA:
                        byte[] data = intent.getByteArrayExtra(EXTRA_DATA);
                        if (data != null) {
                            sendData(data);
                        }
                        break;
                    case ACTION_DISCONNECT:
                        disconnect();
                        break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void startForegroundService() {
        createNotificationChannel();
        final Notification notification = createNotification("Bluetooth Service is Running...");
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private Notification createNotification(String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BluetoothJoystick Service")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_connected) // Replace with your icon
                .setContentIntent(pendingIntent)
                .build();
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice() {
        System.out.println("Connecting to " + macAddress + ", service: " + serviceUid + ", characteristic: " + characteristicUid);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    @SuppressLint("MissingPermission")
    private void sendData(byte[] data) {
        if (characteristic != null) {
            bluetoothGatt.writeCharacteristic(characteristic, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
    }


    @SuppressLint("MissingPermission")
    private void disconnect() {
        if (bluetoothGatt != null) {
            refreshDeviceCache(bluetoothGatt);
            bluetoothGatt.disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e(TAG, "An exception occurred while refreshing device");
        }
        return false;
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                Log.d(TAG, "Connected to GATT server.");
                // Broadcast connection status
                broadcastConnectionStatus(true);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.");
                // Broadcast disconnection status
                broadcastConnectionStatus(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                service = gatt.getService(serviceUid);
                if (service == null) {
                    Log.e(TAG, "Unable to find service: " + serviceUid);
                    return;
                }
                characteristic = service.getCharacteristic(characteristicUid);
                if (characteristic == null) {
                    Log.e(TAG, "Unable to find characteristic: " + characteristicUid);
                    return;
                }
                Log.d(TAG, "Service and characteristic discovered.");
                setCharacteristicNotification(characteristic, true);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            if (characteristic.getUuid().equals(characteristicUid)) {
                processReceivedData(value);
            }
        }

    };

    private void broadcastConnectionStatus(boolean isConnected) {
        Intent intent = new Intent(ACTION_CONNECTION_STATUS);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_IS_CONNECTED, isConnected);
        sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        bluetoothGatt.close();

        stopForegroundService();

        bluetoothGatt = null;
        characteristic = null;
    }

    @SuppressLint("MissingPermission")
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); // Client Characteristic Configuration Descriptor
        if (descriptor != null) {
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private void processReceivedData(byte[] data) {
        String receivedText = new String(data, StandardCharsets.UTF_8); // Assuming UTF-8 encoding
        Log.d(TAG, "Received data: " + receivedText);
        // Broadcast the received text
        broadcastReceivedText(receivedText);
    }

    private void broadcastReceivedText(String text) {
        Intent intent = new Intent(ACTION_RECEIVED_DATA);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_RECEIVED_TEXT, text);
        sendBroadcast(intent);
    }
}
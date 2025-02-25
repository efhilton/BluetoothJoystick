# Bluetooth Joystick Helper

![image](https://github.com/user-attachments/assets/51b72aab-a875-41ea-9417-1bc484232290)


## Overview

This is a simple Android utility that allows the user to connect to a remote bluetooth device (such as a robot)
and control it via a series of toggles, buttons, and joysticks. 

This only works on Android v15 and higher.

Settings can be found in the settings tab, where you specify the Bluetooth connection items. Specifically, you must specify:

- Bluetooth MAC Address
- Service ID
- Character ID

This app is being written in conjunction with:

- an [ESP32 Library](https://github.com/efhilton/BluetoothJoystickLibraryESP32).  You must add this within your `components` folder.
- a [simple ESP32-S3 testing application](https://github.com/efhilton/BluetoothJoystickLibraryESP32Test). You can use this as an example on how to use the library.

## Features
This app contains the following features:

- Up to 15 Function toggle buttons via which you can enable/disable any functions on the remote device. Eg. "Enable Line Following Feature"
- Up to 15 Trigger buttons via which you can trigger events in the remote device. Eg. "Shoot".
- Left and Right Joysticks, normalized to +/- 1.0. This gives you four channels of analog values to control your remote device.
- Timestamped Console output containing both local and remote console messages. For example, the device can send a message back showing "A Collision was detected".

## Connections
As of the time of this writing, you can only connect to a single device if you know its MAC address, its Service ID, and its Characteristic ID.  

In future versions, I intend to create "profiles" which allows you to save these, so that you can jump across different devices. 

## Questions?

Please contact me at [edgar.hilton@gmail.com](mailto:edgar.hilton@gmail.com)

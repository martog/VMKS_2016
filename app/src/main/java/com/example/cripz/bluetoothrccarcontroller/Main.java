package com.example.cripz.bluetoothrccarcontroller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class Main extends MenuActivity {

    FragmentManager manager;
    public static ImageButton carLights;
    private Boolean longLightsFlag = false;
    public static Boolean shortLightsFlag = false;
    private int lightInt;
    private int distanceInt;
    private String mDeviceName;
    private String mDeviceAddress;
    private static RBLService mBluetoothLeService;
    private boolean eventHappenFlag = false;
    private boolean actionFlag = false;
    private static Main mainInstance = null;
    ImageView batteryView;
    private static HashMap<UUID, BluetoothGattCharacteristic> map = new HashMap<>();
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public boolean eventHappen(String sSign, String sType, String sValue) {
        if (sType.equals("distance")) {
            switch (sSign) {
                case "<":
                    if (distanceInt < Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case ">":
                    if (distanceInt > Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case "=":
                    if (distanceInt == Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
            }
        } else if (sType.equals("light")) {
            switch (sSign) {
                case "<":
                    if (lightInt < Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case ">":
                    if (lightInt > Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
                case "=":
                    if (lightInt == Integer.parseInt(sValue)) {
                        eventHappenFlag = true;
                    } else {
                        eventHappenFlag = false;
                    }
                    break;
            }
        }
        Log.d("aaa", "eventHappenFlag: " + eventHappenFlag + " <-----");
        return eventHappenFlag;
    }

    public void doAction(String action) {
        switch (action) {
            case "go_forward":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                break;
            case "go_backward":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                break;
            case "go_forward_right":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("r"); //go right
                break;
            case "go_forward_left":
                sendMessage("f"); //go forward
                sendMessage("g"); //stop backward
                sendMessage("l"); //go left
                sendMessage("j"); //stop right
                break;
            case "go_backward_right":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("h"); //stop left
                sendMessage("r"); //go right
                break;
            case "go_backward_left":
                sendMessage("k"); //stop forward
                sendMessage("b"); //go backward
                sendMessage("l"); //go left
                sendMessage("j"); //stop right
                break;
            case "stay":
                sendMessage("v"); //lights_off
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                Log.d("aaa", "Stay called...");
                break;

            case "lights_on":
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                sendMessage("n"); //lights_on
                break;
            case "lights_off":
                sendMessage("k"); //stop forward
                sendMessage("g"); //stop backward
                sendMessage("h"); //stop left
                sendMessage("j"); //stop right
                sendMessage("v"); //lights_off

        }
    }

    private void displayData(byte[] data) {

        String bytesAsString = null;
        try {
            bytesAsString = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (bytesAsString != null) {
            //Log.d("fr", "bytesAsString:  " + bytesAsString);
            if (bytesAsString.contains("d")) {
                TextView distance = (TextView) findViewById(R.id.distanceId);
                bytesAsString = bytesAsString.split("d")[1].split(" ")[0];
                // Log.d("fr", "Distance:  " + bytesAsString);
                distanceInt = Integer.parseInt(bytesAsString);
                distance.setText("Distance: " + bytesAsString + " cm");
            } else if (bytesAsString.contains("l")) {
                TextView light = (TextView) findViewById(R.id.lightId);
                bytesAsString = bytesAsString.split("l")[1].split(" ")[0];
                Log.d("fr", "Light:  " + bytesAsString);
                light.setText("Light: " + bytesAsString + " lux");
                lightInt = Integer.parseInt(bytesAsString);
            } else if (bytesAsString.contains("b")) {
                bytesAsString = bytesAsString.split("b")[1].split(" ")[0];
                Log.d("fr", "Battery:  " + bytesAsString);
                setBatteryImage(Float.parseFloat(bytesAsString));
            }

            if (ProgrammingFragment.runProgramFlag) {
                if (ProgrammingFragment.condition.equals("Wait for event to happen and then do action")) {
                    Log.d("aaa", "Wait for event...");
                    if (eventHappen(ProgrammingFragment.sensorSign, ProgrammingFragment.sensorType, ProgrammingFragment.sensorValue)) {
                        doAction(ProgrammingFragment.action);
                        ProgrammingFragment.runProgramFlag = false;
                        Log.d("asd", "event: True");
                    }
                } else if (ProgrammingFragment.condition.equals("Do action until event happens")) {
                    if (!actionFlag) {
                        doAction(ProgrammingFragment.action);
                        actionFlag = true;
                    }
                    if (eventHappen(ProgrammingFragment.sensorSign, ProgrammingFragment.sensorType, ProgrammingFragment.sensorValue)) {
                        doAction("stay");
                        ProgrammingFragment.runProgramFlag = false;
                        actionFlag = false;
                    }
                }
            }
        }
    }


    private void setBatteryImage(float voltage) {
        if (voltage > 4.00) {
            batteryView.setImageResource(R.drawable.battery_full);
        } else if (voltage > 2.80) {
            batteryView.setImageResource(R.drawable.battery_mid);
        } else if (voltage > 2.10) {
            batteryView.setImageResource(R.drawable.battery_low);
        } else if (voltage < 2.10) {
            batteryView.setImageResource(R.drawable.battery_empty);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mainInstance);
                builder.title("The device has been disconnected.");
                builder.neutralText("Exit");
                builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                });
                builder.cancelable(false);
                builder.show();
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };

    protected void sendMessage(String arg) {
        BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
        characteristic.setValue(arg);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mainInstance = this;
        mDeviceAddress = getIntent().getStringExtra(Devices.EXTRA_DEVICE_ADDRESS);
        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        initializeCarLightsButton();
        batteryView = (ImageView) findViewById(R.id.batteryId);
        manager = getSupportFragmentManager();
        startCurrentMode(getCurrentMode());
    }

    private void initializeCarLightsButton() {
        carLights = (ImageButton) findViewById(R.id.car_lights);

        carLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!longLightsFlag) {
                        if (shortLightsFlag) {
                            carLights.setBackgroundResource(R.drawable.short_off);
                            sendMessage("v");
                            shortLightsFlag = false;
                        } else {
                            carLights.setBackgroundResource(R.drawable.short_on);
                            sendMessage("n");
                            shortLightsFlag = true;
                        }
                    }
                }
        });

        carLights.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longLightsFlag) {
                    carLights.setBackgroundResource(R.drawable.short_on);
                    sendMessage("n");
                    longLightsFlag = false;
                } else {
                    carLights.setBackgroundResource(R.drawable.long_on);
                    sendMessage("m");
                    longLightsFlag = true;
                }
                return true;
            }
        });
    }

    private void startCurrentMode(int mode) {
        FragmentTransaction transaction = manager.beginTransaction();
        switch (mode) {
            case 0:
                ControlsFragment controlsFragment = new ControlsFragment();
                transaction.replace(R.id.fragment_container, controlsFragment);
                transaction.commit();
                break;
            case 1:
                AccelerometerFragment accelFragment = new AccelerometerFragment();
                transaction.replace(R.id.fragment_container, accelFragment);
                transaction.commit();
                break;
            case 2:
                ProgrammingFragment programmingFragment = new ProgrammingFragment();
                transaction.replace(R.id.fragment_container, programmingFragment);
                transaction.commit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        carLights.setBackgroundResource(R.drawable.short_off);
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.controls:
                putCurrentMode(0);
                ControlsFragment controlsFragment = new ControlsFragment();
                transaction.replace(R.id.fragment_container, controlsFragment);
                transaction.commit();
                return true;
            case R.id.accelerometer:
                putCurrentMode(1);
                AccelerometerFragment accelFragment = new AccelerometerFragment();
                transaction.replace(R.id.fragment_container, accelFragment);
                transaction.commit();
                return true;
            case R.id.programming:
                putCurrentMode(2);
                ProgrammingFragment programmingFragment = new ProgrammingFragment();
                transaction.replace(R.id.fragment_container, programmingFragment);
                transaction.commit();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
        unbindService(mServiceConnection);
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }
}

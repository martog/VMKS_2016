package com.example.cripz.bluetoothrccarcontroller;

import android.content.res.Configuration;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class Devices extends MenuActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000;
    private MaterialDialog processDialog;
    public ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    public static Devices devicesInstance = null;
    public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentOrientation = this.getResources().getConfiguration().orientation;
        if(currentOrientation==Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.devices_activity_portrait_mode);
        }else if(currentOrientation==Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.devices_activity_landscape_mode);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        //checks if the device supports BLE
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_LONG).show();
            finish();
        }

        devicesInstance = this;

        // Initializes Bluetooth adapter.
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is turned on. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            searchForAvailableDevices();
        }

        Button btn = (Button) findViewById(R.id.main_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.devices_activity_portrait_mode);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.devices_activity_landscape_mode);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    public void buildRoundProcessDialog(Context mContext) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.content(R.string.progress_dialog);
        builder.progress(true, 0);
        builder.cancelable(false);
        processDialog = builder.build();
    }

    private void searchForAvailableDevices() {
        buildRoundProcessDialog(devicesInstance);
        scanLeDevice();
        processDialog.show();

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                showAvailableDevices();
            }
        }, SCAN_PERIOD);
    }

    private void showAvailableDevices() {

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(devicesInstance);
        if (mDevices.size() > 0) {
            final String[] devicesName = new String[mDevices.size()];
            final String[] devicesAddr = new String[mDevices.size()];

            for (int i = 0; i < mDevices.size(); i++) {
                devicesName[i] = mDevices.get(i).getName();
                devicesAddr[i] = mDevices.get(i).getAddress();
            }
            builder.title("Available Devices");
            builder.items(devicesName);
            builder.cancelable(false);
            builder.itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    String addr = devicesAddr[which];
                    intent = new Intent(Devices.this, Main.class);
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, addr);
                }
            });
        } else {
            builder.title("No available devices found.");
            builder.cancelable(false);
            builder.positiveText("Retry");
            builder.negativeText("Exit");
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    searchForAvailableDevices();
                }
            });
            builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    finish();
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processDialog.dismiss();
                builder.show();
            }
        });
    }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        // if the device is not in the array
                        if (mDevices.indexOf(device) == -1)
                            mDevices.add(device);
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If user chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            searchForAvailableDevices();
        }
    }
}

package com.example.toggles;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    final int btCode = 110;
    final int wfCode = 111;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sw_wifi;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sw_bt;

    BluetoothAdapter btAdapter;
    WifiManager wfManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        wfManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        checkPermissions();

        sw_wifi = findViewById(R.id.switch_wifi);
        sw_bt = findViewById(R.id.switch_bt);

        sw_bt.setChecked(false);
        sw_wifi.setChecked(false);

        sw_bt.setOnClickListener(this);
        if (btAdapter.isEnabled()) {
            sw_bt.setChecked(true);
        }

        sw_wifi.setOnClickListener(this);
        if (wfManager.isWifiEnabled()) {
            sw_wifi.setChecked(true);
        }

        Log.i("records", Arrays.toString(readSettings()));

    }

    void writeSettings() {
        byte[] bt = new byte[8];

        if (sw_bt.isChecked()) {
            bt[0] = 1;
        }

        if (sw_wifi.isChecked()) {
            bt[1] = 1;
        }

        FileOutputStream stream;
        try {
            stream = openFileOutput("records", MODE_PRIVATE);
            stream.write(bt);
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("records", Arrays.toString(readSettings()));
    }

    byte[] readSettings() {
        byte[] bt = new byte[8];

        FileInputStream stream;
        try {
            stream = openFileInput("records");
            stream.read(bt);
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bt;
    }

    void checkPermissions() {
        checkBtPermissions();
        checkWifiPermissions();
    }

    //檢查Wifi權限
    boolean checkWifiPermissions() {
        ArrayList<String> buffer = new ArrayList<String>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            buffer.add(Manifest.permission.CHANGE_WIFI_STATE);
        }

        if (buffer.size() == 0) {
            return true;
        }

        String[] tmp = (String[]) buffer.toArray();
        requestPermissions(tmp, wfCode);

        return false;
    }

    //檢查藍芽權限
    boolean checkBtPermissions() {
        ArrayList<String> buffer = new ArrayList<String>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                buffer.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (buffer.size() == 0) {
            return true;
        }

        String[] tmp = (String[]) buffer.toArray();
        requestPermissions(tmp, btCode);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == btCode) {
            checkBtPermissions();
        }

        if (requestCode == wfCode) {
            checkWifiPermissions();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.switch_bt:
                if (!checkBtPermissions()) {
                    return;
                }

                if (sw_bt.isChecked()) {
                    btAdapter.enable();
                } else {
                    btAdapter.disable();
                }
                break;
            case R.id.switch_wifi:
                if (!checkWifiPermissions()) {
                    return;
                }

                wfManager.setWifiEnabled(sw_wifi.isChecked());
                break;
            default:
                break;
        }

        writeSettings();
    }
}
package com.example.toggles;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AlarmManagerCompat;

import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.widget.Toast;

public class BootUpReceiver extends BroadcastReceiver {

    BluetoothAdapter btAdapter;
    WifiManager wfManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED":
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                wfManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

                setState(context, readSettings(context));

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Intent i = new Intent(context, BootUpReceiver.class);
                i.setAction("boot");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, pendingIntent);
                break;
            case "boot":
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                wfManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

                setState(context, readSettings(context));
                Toast.makeText(context, "Complete!", Toast.LENGTH_SHORT).show();
                break;
            case "android.intent.action.QUICKBOOT_POWERON":
                Log.i("BootReceiver", "not used state");
                break;
            default:
                Log.i("BootReceiver", "not used state");
                break;
        }
    }

    byte[] readSettings(Context context) {
        byte[] bt = new byte[8];

        FileInputStream stream;
        try {
            stream = context.openFileInput("records");
            stream.read(bt);
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bt;
    }

    void setState(Context context, byte[] bt) {

        wfManager.setWifiEnabled(bt[1] == 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (bt[0] == 1) {
            btAdapter.enable();
        } else {
            btAdapter.disable();
        }

    }
}

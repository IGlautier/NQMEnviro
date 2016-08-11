package com.nqm.nqmenviro;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ivan on 10/08/2016.
 */
public class EnviroService extends Service { // Background service that keeps listening for sensor events while minimised
    DataExporter exporter;
    LocalSensors localSensors;

    @Override
    public void onCreate() {
        localSensors = new LocalSensors(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        localSensors.addSensor(Sensor.TYPE_LIGHT);
        localSensors.addSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        localSensors.addSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        localSensors.addSensor(Sensor.TYPE_PRESSURE);
        exporter = new DataExporter(intent.getStringExtra("name") + ":" + intent.getStringExtra("secret"), this, intent.getStringExtra("asset"), intent.getBooleanExtra("create", false));
        exporter.getAccessToken();
        startSend();
        return START_NOT_STICKY;
    }

    public void startSend() {
        final Handler h = new Handler();
        final int delay = 60000; //milliseconds
        h.postDelayed(new Runnable(){
            public void run(){
                exporter.exportData(localSensors.createJson(System.currentTimeMillis()));
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    public void stopSensors() {
        localSensors.destroy();
        stopSelf();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        localSensors.destroy();
    }
}

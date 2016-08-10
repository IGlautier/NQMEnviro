package com.nqm.nqmenviro;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ivan on 10/08/2016.
 */
public class EnviroService extends Service { // Background service that keeps listening for sensor events while minimised
    DataExporter dataExporter;
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
        return START_NOT_STICKY;
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

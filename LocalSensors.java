package com.nqm.nqmenviro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 10/08/2016.
 */
public class LocalSensors implements SensorEventListener { // Handles sensor events

    private Context context;

    // Holds all sensor readings until they are stored
    private SensorReading lastTemp;
    private SensorReading lastLight;
    private SensorReading lastPrsr;
    private SensorReading lastHmty ;

    // Controls access to sensors
    private SensorManager sensorManager;
    private List<Sensor> sensors;

    public LocalSensors (Context _context) {
        this.context = _context;
        // Sensors setup
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public boolean addSensor(int sensorType) {
        return sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();

        switch(type) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE :
                lastTemp = new SensorReading(event.values[0], event.timestamp);
                break;
            case Sensor.TYPE_LIGHT :
                lastLight = new SensorReading(event.values[0], event.timestamp);
                break;
            case Sensor.TYPE_PRESSURE :
                lastPrsr = new SensorReading(event.values[0], event.timestamp);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY :
                lastHmty = new SensorReading(event.values[0], event.timestamp);
                break;

            default :                break;
        }

    }

    public JSONObject createJson(int time) {
        JSONObject json = new JSONObject();
        try {
            json.put("timestamp", time);
            json.put("light", lastLight.getValue());
            json.put("pressure", lastPrsr.getValue());
            json.put("humidity", lastHmty.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void destroy() {
        for (Sensor it : sensors) {
            sensorManager.unregisterListener(this);
        }
    }

}

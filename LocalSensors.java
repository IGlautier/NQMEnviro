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
        lastTemp = new SensorReading(0, System.currentTimeMillis());
        lastLight = new SensorReading(0, System.currentTimeMillis());
        lastPrsr = new SensorReading(0, System.currentTimeMillis());
        lastHmty = new SensorReading(0, System.currentTimeMillis());

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

    public JSONArray createJson(long time) {
        JSONArray data = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            json.put("timestamp", time);
            json.put("light", lastLight.getValue());
            json.put("pressure", lastPrsr.getValue());
            json.put("humidity", lastHmty.getValue());
            json.put("temperature", lastTemp.getValue());
            data.put(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void destroy() {
        for (Sensor it : sensors) {
            sensorManager.unregisterListener(this);
        }
    }

}

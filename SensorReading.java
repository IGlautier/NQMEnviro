package com.nqm.nqmenviro;

/**
 * Created by Ivan on 10/08/2016.
 */
public class SensorReading {

    private float value;
    private long timestamp;

    public SensorReading() {
        this.value = 0;
        this.timestamp = 0;
    }

    public SensorReading (float _value, long _timestamp) {
        this.value = _value;
        this.timestamp = _timestamp;
    }

    public float getValue() {
        return this.value;
    }

    public long getTime() {
        return this.timestamp;
    }

}

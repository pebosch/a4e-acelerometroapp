package com.a4e.acelerometroapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

public class AccelerometerHandler implements SensorEventListener{
	private float accelX;
    private float accelY;
    private float accelZ;
    private long time;
    private float power;
    private Sensor accelerometer;

    public AccelerometerHandler(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); //el manager que se va a usar para el listener

        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            accelerometer = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No hacer nada
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];
            time = event.timestamp;
            power = accelerometer.getPower();
        }
    } // Almacena los valores del acelerometro desde array event.values

    public float getAccelX() {
        return accelX;
    }

    public float getAccelY() {
        return accelY;
    }

    public float getAccelZ() {
        return accelZ;
    }

    public long getAtTime(){
    	return time;
    }
    
    public float getPower(){
    	return power;
    }
}

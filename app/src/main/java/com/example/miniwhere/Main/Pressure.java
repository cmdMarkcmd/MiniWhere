package com.example.miniwhere.Main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.text.DecimalFormat;

public class Pressure {

    private MainActivity mainActivity;
    private SensorManager sensorManager = null;
    private Sensor mPressure;
    private Sensor mAccelerate;
    private SensorEventListener pressureListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            float sPV = event.values[0];
            float sPV2 = (float) ((float) (int)(sPV*10) / 10.0);
            mainActivity.setPressure("气压",String.valueOf(sPV2)+"百帕");
            DecimalFormat df = new DecimalFormat("0.00");
            df.getRoundingMode();
//             计算海拔
            double height = 44330000*(1-(Math.pow((Double.parseDouble(df.format(sPV))/1013.25),
                    (float)1.0/5255.0)));
            height = (double) ((double) (int)(height*100) /100.0);
            mainActivity.setAltitude("海拔",df.format(height)+"米");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };
    public Pressure(Context context){
        mainActivity = (MainActivity) context;
    }

    void RegisterPressure(){
        sensorManager.registerListener(pressureListener, mPressure,
                SensorManager.SENSOR_DELAY_NORMAL);

    }
    void UnregisterPressure(){
        if(pressureListener!=null){
            sensorManager.unregisterListener(pressureListener);
        }
    }
    void InitPressure(){
        sensorManager = (SensorManager)mainActivity.getSystemService(Context.SENSOR_SERVICE);
        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(mPressure == null) return;
        mAccelerate = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
}

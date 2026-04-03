package com.example.app3;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, lightSensor, proximitySensor;

    private TextView accelText, lightText, proximityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize UI components
        accelText = findViewById(R.id.accel_text);
        lightText = findViewById(R.id.light_text);
        proximityText = findViewById(R.id.proximity_text);

        // 2. Initialize Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 3. Define Sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // 4. Check if sensors exist on this specific device
        if (accelerometer == null) {
            accelText.setText("Accelerometer: Not Found");
            accelText.setTextColor(Color.RED);
        }
        if (lightSensor == null) {
            lightText.setText("Light Sensor: Not Found");
            lightText.setTextColor(Color.RED);
        }
        if (proximitySensor == null) {
            proximityText.setText("Proximity Sensor: Not Found");
            proximityText.setTextColor(Color.RED);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                // Format to 2 decimal places for better UI readability
                accelText.setText(String.format("X: %.2f\nY: %.2f\nZ: %.2f",
                        event.values[0], event.values[1], event.values[2]));
                break;

            case Sensor.TYPE_LIGHT:
                lightText.setText("Ambient Light: " + event.values[0] + " lx");
                break;

            case Sensor.TYPE_PROXIMITY:
                float distance = event.values[0];
                proximityText.setText("Distance: " + distance + " cm");

                // Visual feedback for the "5.0 vs 0.0" issue
                if (distance < event.sensor.getMaximumRange()) {
                    proximityText.setTextColor(Color.BLUE); // Near
                } else {
                    proximityText.setTextColor(Color.BLACK); // Far
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not required for basic reading
    }

    @Override
    protected void onResume() {
        super.onResume();
        // CRITICAL: You must register EVERY sensor here, otherwise they won't update
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (proximitySensor != null) {
            // Using SENSOR_DELAY_UI for a balance of battery and speed
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregistering stops all sensors to save battery
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
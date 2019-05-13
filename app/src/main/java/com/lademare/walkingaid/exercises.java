package com.lademare.walkingaid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class exercises extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    BluetoothAdapter myBluetoothAdapter;
    boolean running = false;
    boolean ex_1;
    boolean ex_2;
    boolean ex_3;
    boolean ex_1_start = false;
    boolean ex_2_start = false;
    boolean ex_3_start = false;
    public static final String ex_1_status = "ex_1_status";
    public static final String ex_2_status = "ex_2_status";
    public static final String ex_3_status = "ex_3_status";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_exercises);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        menu();
        checkexercisestatus();
        startexercises();
    }

    protected void menu() {
        Button btn_data = findViewById(R.id.btn_data);
        Button btn_profile = findViewById(R.id.btn_profile);
        TextView btn_exercises = findViewById(R.id.btn_exercises);
        btn_exercises.setTextColor(Color.WHITE);
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercises.this, data.class));
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercises.this, profile.class));
            }
        });
        ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercises.this, bluetooth.class));
            }
        });
    }

    protected void checkexercisestatus() {
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        ex_1 = sp.getBoolean(ex_1_status, false);
        ex_2 = sp.getBoolean(ex_2_status, false);
        ex_3 = sp.getBoolean(ex_3_status, false);
        ToggleButton btn_ex_1 = findViewById(R.id.btn_ex_1);
        if (ex_1){
            btn_ex_1.setChecked(true);
            ex_1 = true;
        } else{ btn_ex_1.setChecked(false);}
        ToggleButton btn_ex_2 = findViewById(R.id.btn_ex_2);
        if (ex_2){
            btn_ex_2.setChecked(true);
            ex_2 = true;
        } else{ btn_ex_2.setChecked(false);}
        ToggleButton btn_ex_3 = findViewById(R.id.btn_ex_3);
        if (ex_3){
            btn_ex_1.setChecked(true);
            ex_3 = true;
        } else{ btn_ex_3.setChecked(false);}
    }

    protected void startexercises() {
        ToggleButton btn_ex_1 = findViewById(R.id.btn_ex_1);;
        btn_ex_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ex_1) {
                    Toast.makeText(getApplicationContext(),"Exercise 1 stopped",Toast.LENGTH_SHORT).show(); ex_1_start = false;
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 1 started",Toast.LENGTH_SHORT).show(); ex_1_start = true;}
                }
                ex_1 = !ex_1;
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_1_status",  ex_1);
                editor.apply();
            }
        });
        ToggleButton btn_ex_2 = findViewById(R.id.btn_ex_2);
        btn_ex_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ex_2) {
                    Toast.makeText(getApplicationContext(),"Exercise 2 stopped",Toast.LENGTH_SHORT).show(); ex_2_start = false;
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 2 started",Toast.LENGTH_SHORT).show(); ex_2_start = true; }
                }
                ex_2 = !ex_2;
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_2_status",  ex_2);
                editor.apply();
            }
        });
        ToggleButton btn_ex_3 = findViewById(R.id.btn_ex_3);
        btn_ex_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ex_3) {
                    Toast.makeText(getApplicationContext(),"Exercise 3 stopped",Toast.LENGTH_SHORT).show(); ex_3_start = false;
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                    else{ Toast.makeText(getApplicationContext(),"Exercise 3 started",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                }
                ex_3 = !ex_3;
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_3_status",  ex_3);
                editor.apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);   // look if sensor is available on phone
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();   // notice users if there phone doesn't have the sensor
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            if(ex_2_start){
                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.isWiredHeadsetOn()){
                    Toast.makeText(this, "headphone detected", Toast.LENGTH_SHORT).show();
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(false);
                }
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
            }
            if(ex_3_start){
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}

package com.lademare.walkingaid;

/*
Bluetooth code using tutorial: Android Bluetooth Connectivity Tutorial - Sarthi Technology and example: Simple Android Bluetooth Application with Arduino Example - justin bauer
Rrite to file code made using example of Javed Khan from Creative Apps
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import android.os.Handler;

public class exercisespt1 extends AppCompatActivity {

    SensorManager sensorManager;
    boolean ex_1;
    boolean ex_2;
    boolean ex_3;
    boolean ex_1_start = false;
    boolean ex_2_start = false;
    boolean ex_3_start = false;
    public static final String ex_1_status = "ex_1_status"; // status is saved in shared preferences to be able to switch activities
    public static final String ex_2_status = "ex_2_status";
    public static final String ex_3_status = "ex_3_status";
    public static final String average_aid = "old_average_aid";
    public static final String average_step = "old_average_step";

    BluetoothAdapter myBluetoothAdapter;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private final String TAG = exercisespt1.class.getSimpleName();
    private BluetoothSocket BTSocket1 = null;
    private BluetoothSocket BTSocket2 = null;
    private exercisespt1.ConnectedThread1 mConnectedThread1;
    private exercisespt1.ConnectedThread2 mConnectedThread2;
    private Handler BTHandler1;
    Intent btEnablingIntent;
    int requestCodeForEnable;
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    public static final String walkingaid = "walkingaid";

    String readMessage = " ";
    String result;
    int time_ex = 0;

    int time_step = 0;
    int new_measurement_step = 0;
    int old_average_step;
    int new_average_step;
    int timer_step = 0;
    String walkinggait;
    int time_footaid = 0;
    int new_measurement_aid = 0;
    int old_average_aid;
    int new_average_aid;

    boolean rhythmconsistent = true;
    int offrhythm = 0;
    int timer_offrhythm = 30;
    int timer_feedback = 10;

    boolean handler_running = false;
    boolean newstart;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_exercises_pt1);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        menu();
        checkexercisestatus();
        startexercises();

        ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
        TextView input1 = findViewById(R.id.input1);
        if (myBluetoothAdapter.isEnabled()){
            btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            input1.setVisibility(View.VISIBLE);
            createsocket(); // connect to device if bluetooth is already on
        } else {
            btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.transparent));
            findViewById(R.id.input1); input1.setVisibility(View.INVISIBLE);
        }

        Button getdata = findViewById(R.id.getdata);
        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercisespt1.this, getdata.class));
            }
        });

        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        old_average_aid = sp.getInt(average_aid, 15);
        old_average_step = sp.getInt(average_step, 25);


        BTHandler1 = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    try {
                        handler_running = true;
                        readMessage = new String((byte[]) msg.obj, "US-ASCII");
                        practice();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    TextView input1 = findViewById(R.id.input1);
                    input1.setText(readMessage);
                } else {
                    handler_running = false;
                }
            }
        };
    }

    protected void practice() {
        if (readMessage.contains("foot")){
            result = "foot";
        } else if (readMessage.contains("aid")){
            result = "aid";
        } else {
            result = " ";
        }

        if (ex_1_start||ex_2_start||ex_3_start){
            time_ex++; // timer to see how long the exercise is been going to determine fase
            if (newstart){
                Toast.makeText(getApplicationContext(), "newstart", Toast.LENGTH_SHORT).show();
                Context context = getApplicationContext();
                writedatatofile(context);
                newstart = false;
            }
            if (time_ex > 50) { // start measuring after certain time to allow the user to store the phone away first
                time_step++;
                time_footaid++;
                if (result.equals("aid")) {
                    time_footaid = 0;
                }
                if (result.equals("foot")) {
                    // average_step
                    new_measurement_step = time_step;
                    if ((new_measurement_step / old_average_step) > 3) { // don't take outliers into account, can be caused by extern source
                        new_measurement_step = old_average_step;
                        Toast.makeText(getApplicationContext(), "outlier", Toast.LENGTH_SHORT).show();
                    }
                    time_step = 0;
                    new_average_step = ((9 * old_average_step + new_measurement_step) / 10); // approximate average, last measurements have most impact
                    old_average_step = new_average_step;
                    // average_aid
                    new_measurement_aid = time_footaid;
                    if ((new_measurement_aid/old_average_aid)>3){ // don't take outliers into account, these can be caused by extern source, like waiting to cross the road
                        new_measurement_aid = old_average_aid;
                        Toast.makeText(getApplicationContext(),"outlier",Toast.LENGTH_SHORT).show();
                    }
                    new_average_aid = ((9 * old_average_aid + new_measurement_aid) / 10); // approximate average, last measurements have most impact
                    old_average_aid = new_average_aid;
                }

                if (new_average_aid < 3){
                    walkinggait = "2-point";
                } else {
                    walkinggait = "3-point";
                }
            }

            if (time_ex > 100) { // give feedback after some data is collected to have some valid input

                if (result.equals("foot")){
                    Context context = getApplicationContext();
                    writedatatofile(context);
                }

                timer_offrhythm--;
                if (((new_measurement_step / old_average_step) < 0.9) || ((new_measurement_step / old_average_step) > 1.1) && (result.equals("heel-strike"))) { // check if the new step is in rhythm or not
                    timer_offrhythm = 30; // check is there are multible steps not in rhythm in a certain amount of time
                    offrhythm = offrhythm + 1;
                }
                if (timer_offrhythm <= 0) {
                    offrhythm = 0;
                    timer_offrhythm = 30;
                }
                if (offrhythm >= 3) {
                    rhythmconsistent = false;
                }
                timer_step++;

                if (timer_step >= new_average_step) { // make sounds in rhythm of average
                    timer_step = 0;
                    if ((time_ex < 200) || (!rhythmconsistent)) { // give input in the beginning and when the rhythm is not constant
                        timer_feedback--; // give input at least 10 times to get back in rhythm
                        if (timer_feedback == 0){
                            timer_feedback = 10;
                            rhythmconsistent = true;
                        }
                        if (ex_1_start) {
                            final MediaPlayer biep1 = MediaPlayer.create(this, R.raw.biep1);
                            biep1.start();
                        }
                        if (ex_2_start) {
                            final MediaPlayer foot = MediaPlayer.create(this, R.raw.foot);
                            foot.start();
                        }
                        if (ex_3_start){
                            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                        }
                    }
                }

                if (timer_step == new_average_aid && walkinggait.equals("3-point")) { // make sounds in rhythm of average
                    if ((time_ex < 200) || (!rhythmconsistent)) { // give input in the beginning and when the rhythm is not constant
                        if (ex_1_start) {
                            final MediaPlayer biep2 = MediaPlayer.create(this, R.raw.biep2);
                            biep2.start();
                        }
                        if (ex_2_start) {
                            SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                            if ((sp.getString(walkingaid, "Stick").equals("Crutch"))) {
                                final MediaPlayer crutch = MediaPlayer.create(this, R.raw.crutch);
                                crutch.start();
                            } else {
                                final MediaPlayer stick = MediaPlayer.create(this, R.raw.stick);
                                stick.start();
                            }
                        }
                        if (ex_3_start){
                            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                        }
                    }
                }
            }
        }

        TextView tvtimer = findViewById(R.id.tvtimer); tvtimer.setText(String.valueOf(timer_step)); // used to check values
        TextView tvaverage = findViewById(R.id.tvaverage); tvaverage.setText(String.valueOf(new_average_step));
        TextView tvaverageaid = findViewById(R.id.tvaverageaid); tvaverageaid.setText(String.valueOf(new_average_aid));
        TextView tvex_time = findViewById(R.id.tvex_time); tvex_time.setText(String.valueOf(time_ex));
        TextView tvoffrhytmtimer = findViewById(R.id.tvoffrhythmtimer); tvoffrhytmtimer.setText(String.valueOf(timer_offrhythm));
        TextView tvoffrhytm = findViewById(R.id.tvoffrhythm); tvoffrhytm.setText(String.valueOf(offrhythm));
        TextView tvfeedbacktimer = findViewById(R.id.tvfeedbacktimer); tvfeedbacktimer.setText(String.valueOf(timer_feedback));
    }

    protected void writedatatofile(Context context){
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_APPEND));
            String data;
            Calendar calender = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            if (newstart){
                String date_time = dateFormat.format(calender.getTime());
                data = (date_time + "Exersice started " + "\n" + "t.s a.s t.a a.a cnst " + "\n");
            } else {
                data = (Integer.toString(time_step)+"  "+Integer.toString(new_average_step)+"  "+Integer.toString(time_footaid)+"  "+Integer.toString(new_average_aid)+"  "+Boolean.toString(rhythmconsistent)+"\n");
            }
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            writer.write(data);
            writer.newLine();
            writer.close();
            Toast.makeText(this, "Data has been written to File", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected void menu() {
        Button btn_data = findViewById(R.id.btn_data);
        Button btn_profile = findViewById(R.id.btn_profile);
        TextView btn_exercises = findViewById(R.id.btn_exercises);
        btn_exercises.setTextColor(Color.WHITE);
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercisespt1.this, datapt1.class));
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercisespt1.this, profilept1.class));
            }
        });
        ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothconnect();
            }
        });
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(exercisespt1.this, login.class));
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
            btn_ex_3.setChecked(true);
            ex_3 = true;
        } else{ btn_ex_3.setChecked(false);}
    }

    protected void startexercises() {
        final ToggleButton btn_ex_1 = findViewById(R.id.btn_ex_1);;
        btn_ex_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myBluetoothAdapter.isEnabled()||!handler_running) { //check if bluetooth is on and if there is a connection, if there isn't give warning and don't start exercise
                    Toast.makeText(getApplicationContext(), "No bluetooth connection", Toast.LENGTH_SHORT).show();
                    btn_ex_1.setChecked(false);
                } else if (ex_1) {
                    Toast.makeText(getApplicationContext(),"Exercise 1 stopped",Toast.LENGTH_SHORT).show();
                    ex_1_start = false;
                    ex_1 = !ex_1;
                    resetvalue();
                } else {
                    Toast.makeText(getApplicationContext(),"Exercise 1 starts in 1 minute",Toast.LENGTH_SHORT).show();
                    ex_1_start = true;
                    ex_1 = !ex_1;
                    newstart = true;
                }
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_1_status",  ex_1);
                editor.apply();
            }
        });
        final ToggleButton btn_ex_2 = findViewById(R.id.btn_ex_2);
        btn_ex_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myBluetoothAdapter.isEnabled()||!handler_running) { //check if bluetooth is on and if there is a connection, if there isn't give warning and don't start exercise
                    Toast.makeText(getApplicationContext(), "No bluetooth connection", Toast.LENGTH_SHORT).show();
                    btn_ex_2.setChecked(false);
                } else if (ex_2) {
                    Toast.makeText(getApplicationContext(),"Exercise 2 stopped",Toast.LENGTH_SHORT).show();
                    ex_2_start = false;
                    ex_2 = !ex_2;
                    resetvalue();
                } else {
                    Toast.makeText(getApplicationContext(),"Exercise 2 starts in 1 minute",Toast.LENGTH_SHORT).show();
                    ex_2_start = true;
                    ex_2 = !ex_2;
                    newstart = true;
                }
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_2_status",  ex_2);
                editor.apply();
            }
        });
        final ToggleButton btn_ex_3 = findViewById(R.id.btn_ex_3);
        btn_ex_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myBluetoothAdapter.isEnabled()||!handler_running) { //check if bluetooth is on and if there is a connection, if there isn't give warning and don't start exercise
                    Toast.makeText(getApplicationContext(), "No bluetooth connection", Toast.LENGTH_SHORT).show();
                    btn_ex_3.setChecked(false);
                } else if (ex_3) {
                    Toast.makeText(getApplicationContext(),"Exercise 3 stopped",Toast.LENGTH_SHORT).show();
                    ex_3_start = false;
                    ex_3 = !ex_3;
                    resetvalue();
                } else {
                    Toast.makeText(getApplicationContext(),"Exercise 3 starts in 1 minute",Toast.LENGTH_SHORT).show();
                    ex_3_start = true;
                    ex_3 = !ex_3;
                    newstart = true;
                }
                ex_3 = !ex_3;
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_3_status",  ex_3);
                editor.apply();
            }
        });
    }

    protected void resetvalue(){
        time_ex = 0;
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("average_aid",  old_average_aid);
        editor.putInt("average_step",  old_average_step);
        editor.apply();
    }


    /* code to enable bluetooth */

    String address1 = ("98:D3:41:FD:3D:B6");
    String address2 = ("98:D3:81:FD:4B:87");
    String name1 = ("Sensor_Foot");
    String name2 = ("Sensor_Aid");


    @SuppressLint("HandlerLeak")
    protected void bluetoothconnect() {
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable=1;
        if (myBluetoothAdapter==null){
            Toast.makeText(getApplicationContext(),"Bluetooth not supported", Toast.LENGTH_LONG).show();
        } else {
            if (!myBluetoothAdapter.isEnabled()) {
                startActivityForResult(btEnablingIntent, requestCodeForEnable);
            }
            if (myBluetoothAdapter.isEnabled()) {
                myBluetoothAdapter.disable();
                Toast.makeText(getApplicationContext(),"Bluetooth disabled",Toast.LENGTH_SHORT).show();
                TextView input1 = findViewById(R.id.input1); input1.setVisibility(View.INVISIBLE);
                ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth); btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==requestCodeForEnable){
            ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"Bluetooth enabled",Toast.LENGTH_SHORT).show();
                TextView input1 = findViewById(R.id.input1); input1.setVisibility(View.VISIBLE);
                btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                createsocket();
            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Bluetooth enabling cancelled",Toast.LENGTH_SHORT).show();
                TextView input1 = findViewById(R.id.input1); input1.setVisibility(View.INVISIBLE);
                btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }
    }

    protected void createsocket() {
        new Thread() {
            public void run() {
                boolean fail = false;

                BluetoothDevice device1 = myBluetoothAdapter.getRemoteDevice(address1);
                try {
                    BTSocket1 = createBluetoothSocket1(device1);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket1 creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    BTSocket1.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        BTSocket1.close();
                        BTHandler1.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket1 creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!fail) {
                    mConnectedThread1 = new ConnectedThread1(BTSocket1);
                    mConnectedThread1.start();
                    BTHandler1.obtainMessage(CONNECTING_STATUS, 1, -1, name1)
                            .sendToTarget();
                }

                BluetoothDevice device2 = myBluetoothAdapter.getRemoteDevice(address2);
                try {
                    BTSocket2 = createBluetoothSocket2(device2);
                } catch (IOException e) {
                    fail = true;
                    //Toast.makeText(getBaseContext(), "Socket2 creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    BTSocket2.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        BTSocket2.close();
                        BTHandler1.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket2 creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!fail) {
                    mConnectedThread2 = new ConnectedThread2(BTSocket2);
                    mConnectedThread2.start();
                    BTHandler1.obtainMessage(CONNECTING_STATUS, 1, -1, name2)
                            .sendToTarget();
                }

            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket1(BluetoothDevice device1) throws IOException {
        try {
            final Method m1 = device1.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m1.invoke(device1, PORT_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device1.createRfcommSocketToServiceRecord(PORT_UUID);
    }

    private BluetoothSocket createBluetoothSocket2(BluetoothDevice device2) throws IOException {
        try {
            final Method m2 = device2.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m2.invoke(device2, PORT_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device2.createRfcommSocketToServiceRecord(PORT_UUID);
    }

    private class ConnectedThread1 extends Thread {
        final BluetoothSocket mmSocket1;
        private final InputStream mmInStream1;

        public ConnectedThread1(BluetoothSocket socket1) {
            mmSocket1 = socket1;
            InputStream tmpIn = null;
            try {
                tmpIn = socket1.getInputStream();
            } catch (IOException e) { }
            mmInStream1 = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream1.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        //SystemClock.sleep(50); //pause and wait for rest of datapt1. Adjust this depending on your sending speed.
                        bytes = mmInStream1.available(); // how many bytes are ready to be read?
                        bytes = mmInStream1.read(buffer, 0, bytes); // record how many bytes we actually read
                        BTHandler1.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class ConnectedThread2 extends Thread {
        final BluetoothSocket mmSocket2;
        private final InputStream mmInStream2;

        public ConnectedThread2(BluetoothSocket socket2) {
            mmSocket2 = socket2;
            InputStream tmpIn = null;
            try {
                tmpIn = socket2.getInputStream();
            } catch (IOException e) { }
            mmInStream2 = tmpIn;
        }
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream2.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        //SystemClock.sleep(50); //pause and wait for rest of datapt1. Adjust this depending on your sending speed.
                        bytes = mmInStream2.available(); // how many bytes are ready to be read?
                        bytes = mmInStream2.read(buffer, 0, bytes); // record how many bytes we actually read
                        BTHandler1.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
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

    BluetoothAdapter myBluetoothAdapter;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private final String TAG = exercisespt1.class.getSimpleName();
    private BluetoothSocket BTSocket = null;
    private exercisespt1.ConnectedThread mConnectedThread;
    private Handler BTHandler;
    Intent btEnablingIntent;
    int requestCodeForEnable;
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    String inputdata1 = " ";
    String readMessage = " ";
    String result;
    boolean newdata = false;

    int ex_time = 0;
    int timebetweensteps = 0;
    int new_measurement = 0;
    int old_average = 10;
    int new_average;
    int timer = 0;
    boolean rhythmconsistent;
    int offrhythm = 0;
    int offrhythmtimer = 30;
    int feedbacktimer = 10;



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

        BTHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    try {
                        readMessage = new String((byte[]) msg.obj, "US-ASCII");
                        practice();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    TextView input1 = findViewById(R.id.input1);
                    input1.setText(readMessage);
                }
            }
        };
    }

    protected void practice() {
        if ((!(inputdata1.equals(readMessage)))&&(newdata)){ // while recognizing the incomming datapt1 didn't work, check if the datapt1 changes
            newdata = false; // while the datapt1 changes 2, from nothing to heel-strike and back, only count the first
            result = "heel-strike";
        }  else {
            newdata = true;
            result = " ";
        }
        inputdata1 = readMessage;

        if (ex_1_start||ex_2_start||ex_3_start){
            ex_time++; // timer to see how long the exercise is been going to determine fase
            if (ex_time > 30){ // start measuring after certain time to allow the user to store the phone away first
                timebetweensteps ++;
                if (result.equals("heel-strike")){
                    new_measurement = timebetweensteps;
                    if ((new_measurement/old_average)>3){ // don't take outliers into account, can be caused by extern source
                        new_measurement = old_average;
                        Toast.makeText(getApplicationContext(),"outlier",Toast.LENGTH_SHORT).show();
                    }
                    timebetweensteps = 0;
                    new_average = ((9*old_average+new_measurement)/10); // approximate average, last measurements have most impact
                    old_average = new_average;
                }
            }
            if (ex_time > 60) { // give feedback after some datapt1 is collected to have some valid input
                offrhythmtimer--;
                if (((new_measurement/old_average)<0.9)||((new_measurement/old_average)>1.1)&&(result.equals("heel-strike"))){ // check if the new step is in rhythm or not
                    offrhythmtimer = 30; // check is there are multible steps not in rhythm in a certain amount of time
                    offrhythm = offrhythm + 1;
                }
                if (offrhythmtimer <= 0){
                    offrhythm = 0;
                    offrhythmtimer = 30;
                }
                if (offrhythm >= 3){
                    rhythmconsistent = false;
                }
                timer++;
                if (timer >= new_average) { // make sounds in rhythm of average
                    timer = 0;
                    if ((ex_time < 120) || (!rhythmconsistent)) { // give input in the beginning and when the rhythm is not constant
                        feedbacktimer--; // give input at least 10 times to get back in rhythm
                        if (feedbacktimer == 0){
                            feedbacktimer = 10;
                            rhythmconsistent = true;
                        }
                        if (ex_1_start) {
                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                            toneG.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, 100); // TONE_CDMA_ABBR_INTERCEPT : soft not to high or low
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
                if (result.equals("heel-strike")){
                    Context context = getApplicationContext();
                    writetofile(context);
                }
            }
        }

        TextView tvtimer = findViewById(R.id.tvtimer); tvtimer.setText(String.valueOf(timer)); // used to check values
        TextView tvaverage = findViewById(R.id.tvaverage); tvaverage.setText(String.valueOf(new_average));
        TextView tvex_time = findViewById(R.id.tvex_time); tvex_time.setText(String.valueOf(ex_time));
        TextView tvoffrhytmtimer = findViewById(R.id.tvoffrhythmtimer); tvoffrhytmtimer.setText(String.valueOf(offrhythmtimer));
        TextView tvoffrhytm = findViewById(R.id.tvoffrhythm); tvoffrhytm.setText(String.valueOf(offrhythm));
        TextView tvfeedbacktimer = findViewById(R.id.tvfeedbacktimer); tvfeedbacktimer.setText(String.valueOf(feedbacktimer));
    }

    protected void writetofile(Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("datawalker.txt", Context.MODE_APPEND));
            String data = ("timebetweensteps = "+ Integer.toString(timebetweensteps)+"\n"+"new_average = "+Integer.toString(new_average)+"\n"+"rhythmconsistent"+Boolean.toString(rhythmconsistent)+"\n");
            data += "\n";
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
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
        ToggleButton btn_ex_1 = findViewById(R.id.btn_ex_1);;
        btn_ex_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ex_1) {
                    Toast.makeText(getApplicationContext(),"Exercise 1 stopped",Toast.LENGTH_SHORT).show(); ex_1_start = false;
                    ex_time = 0; timebetweensteps = 0; new_measurement = 0; old_average = 10; timer = 0; // reset values
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_1_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 1 starts in 1 minute",Toast.LENGTH_SHORT).show(); ex_1_start = true;}
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
                    ex_time = 0; timebetweensteps = 0; new_measurement = 0; old_average = 10; timer = 0;
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_2_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 2 starts in 1 minute",Toast.LENGTH_SHORT).show(); ex_2_start = true; }
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
                    ex_time = 0; timebetweensteps = 0; new_measurement = 0; old_average = 10; timer = 0;
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                    else{ Toast.makeText(getApplicationContext(),"Exercise 3 starts in 1 minute",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
                }
                ex_3 = !ex_3;
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("ex_3_status",  ex_3);
                editor.apply();
            }
        });
    }




    /* code to enable bluetooth */

    String address1 = ("98:D3:41:FD:3D:B6");
    //String address1 = ("98:D3:81:FD:4B:87");
    String name1 = ("Sensor_Shoe");

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

                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address1);

                try {
                    BTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    BTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        BTSocket.close();
                        BTHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!fail) {
                    mConnectedThread = new ConnectedThread(BTSocket);
                    mConnectedThread.start();
                    BTHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name1)
                            .sendToTarget();
                }
            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, PORT_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(PORT_UUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        //SystemClock.sleep(50); //pause and wait for rest of datapt1. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        BTHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
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

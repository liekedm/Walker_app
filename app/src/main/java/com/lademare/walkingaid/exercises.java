package com.lademare.walkingaid;

/*  bluetooth code using tutorial: Android Bluetooth Connectivity Tutorial - Sarthi Technology and example: Simple Android Bluetooth Application with Arduino Example - justin bauer*/

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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class exercises extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
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

    BluetoothAdapter myBluetoothAdapter;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private final String TAG = exercises.class.getSimpleName();
    private BluetoothSocket BTSocket = null;
    private exercises.ConnectedThread mConnectedThread;
    private Handler BTHandler;
    Intent btEnablingIntent;
    int requestCodeForEnable;
    //public String readMessage;
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    String inputdata1 = " ";
    String readMessage = " ";
    String result;
    boolean newdata = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_exercises);
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
        } else {
            btn_bluetooth.setBackgroundColor(getResources().getColor(R.color.transparent));
            findViewById(R.id.input1); input1.setVisibility(View.INVISIBLE);
        }

        BTHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    try {
                        readMessage = new String((byte[]) msg.obj, "US-ASCII");
                        //Log.i("incomming data", readMessage);
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
        if ((!(inputdata1.equals(readMessage)))&&(newdata)){
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
            newdata = false;
            result = "heel-strike";
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    newdata = true;
//                }
//            }, 1000);
        }  else {
            newdata = true;
            result = " ";
        }
        inputdata1 = readMessage;
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
                bluetoothconnect();
                //startActivity(new Intent(exercises.this, bluetooth.class));
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
                } else {
                    if(!myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_1_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 1 starts in 30 seconds",Toast.LENGTH_SHORT).show(); ex_1_start = true;}
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
                        Toast.makeText(getApplicationContext(),"Turn on bluetooth",Toast.LENGTH_SHORT).show(); ex_2_start = true;}
                    else {Toast.makeText(getApplicationContext(),"Exercise 2 starts in 30 seconds",Toast.LENGTH_SHORT).show(); ex_2_start = true; }
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
                    else{ Toast.makeText(getApplicationContext(),"Exercise 3 starts in 30 seconds",Toast.LENGTH_SHORT).show(); ex_3_start = true;}
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
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
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
                        //SystemClock.sleep(50); //pause and wait for rest of data. Adjust this depending on your sending speed.
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

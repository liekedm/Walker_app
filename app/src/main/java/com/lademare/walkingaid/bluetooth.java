//package com.lademare.walkingaid;
//
////bluetooth if (ex_1_start){
//                            ToneGenerator toneGsoft = new ToneGenerator(AudioManager.STREAM_ALARM, 10);
//                            toneGsoft.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
//                        }
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.media.AudioManager;
//import android.media.ToneGenerator;
//import android.os.SystemClock;
//import android.os.Vibrator;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//import android.os.Handler;
//
//import org.w3c.dom.Text;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Method;
//import java.util.Set;
//import java.util.UUID;
//
//public class bluetooth extends AppCompatActivity {
//
//    BluetoothAdapter myBluetoothAdapter;
//    Intent btEnablingIntent;
//    int requestCodeForEnable;
//    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
//    private BluetoothSocket socket;
//    private InputStream inputStream;
//    byte buffer[];
//    boolean stopThread;
//    private final String TAG = bluetooth.class.getSimpleName();
//    private Handler BTHandler;
//    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
//    private BluetoothSocket BTSocket = null; // bi-directional client-to-client data path
//
//    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
//    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
//    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
//
//    public static final String BTinput = "BTinput";
//
//    @SuppressLint("HandlerLeak")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth);
//        overridePendingTransition(0,0);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//
//        menu();
//        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        bluetooth_on_off();
//
//        BTHandler = new Handler(){
////            public void handleMessage(android.os.Message msg){
////                if(msg.what == MESSAGE_READ){
////                    String readMessage = null;
////                    try {
////                        readMessage = new String((byte[]) msg.obj, "UTF-8");
//////                        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
//////                        SharedPreferences.Editor editor = sp.edit();
//////                        editor.putString("BTinput", readMessage);
//////                        editor.apply();
//////                        if ((sp.getString(BTinput, "X").equals("X"))) {
//////                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
//////                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
//////                        }
//////                        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//////                        vibrator.vibrate(100);
////                    } catch (UnsupportedEncodingException e) {
////                        e.printStackTrace();
////                    }
////                    TextView ReadBuffer1 = findViewById(R.id.readBuffer1);
////                    ReadBuffer1.setText(readMessage);
////                }
//
//                if(msg.what == CONNECTING_STATUS){
//                    TextView connect = findViewById(R.id.connected);
//                    if(msg.arg1 == 1) {
//                        Toast.makeText(getApplicationContext(), ("Connected to Device: " + (String) (msg.obj)), Toast.LENGTH_LONG).show();
//                        connect.setText("Connected to Device: " + (String) (msg.obj));
//                    } else{
//                        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
//                        connect.setText("Connection failed");
//                    }
//                }
//            }
//        };
//
////        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
////        if ((sp.getString(BTinput, "X").equals("X"))) {
////            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
////                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
////                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
////                vibrator.vibrate(100);
////        }
//    }
//
//    protected void bluetooth_on_off() {
//        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        requestCodeForEnable=1;
//        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
//        if (myBluetoothAdapter.isEnabled()){
//            btn_btstate.setChecked(true);
//            bluetooth_list();
//            TextView ReadBuffer1 = findViewById(R.id.readBuffer1);
//            ReadBuffer1.setVisibility(View.VISIBLE);
//            TextView connect = findViewById(R.id.connected);
//            connect.setVisibility(View.VISIBLE);
//        } else {
//            btn_btstate.setChecked(false);
//            TextView tv_list = findViewById(R.id.tv_list);
//            tv_list.setVisibility(View.INVISIBLE);
//            TextView ReadBuffer1 = findViewById(R.id.readBuffer1);
//            ReadBuffer1.setVisibility(View.INVISIBLE);
//            TextView connect = findViewById(R.id.connected);
//            connect.setVisibility(View.INVISIBLE);
//        }
//        btn_btstate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myBluetoothAdapter==null){
//                    Toast.makeText(getApplicationContext(),"Bluetooth not supported", Toast.LENGTH_LONG).show();
//                } else{
//                    if (!myBluetoothAdapter.isEnabled()){
//                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
//                    }
//                }
//                if (myBluetoothAdapter.isEnabled()){
//                    myBluetoothAdapter.disable();
//                    TextView tv_list = findViewById(R.id.tv_list);
//                    ListView lv_bt = findViewById(R.id.lv_bt);
//                    tv_list.setVisibility(View.INVISIBLE);
//                    lv_bt.setVisibility(View.INVISIBLE);
//                    TextView ReadBuffer1 = findViewById(R.id.readBuffer1);
//                    ReadBuffer1.setVisibility(View.INVISIBLE);
//                    TextView connect = findViewById(R.id.connected);
//                    connect.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
//        if(requestCode==requestCodeForEnable){
//            if(resultCode==RESULT_OK){
//                bluetooth_list();
//            }
//            else if(resultCode==RESULT_CANCELED){
//                Toast.makeText(getApplicationContext(),"Bluetooth enabling cancelled",Toast.LENGTH_LONG).show();
//                btn_btstate.setChecked(false);
//            }
//        }
//    }
//
//    protected void bluetooth_list() {
//        ListView lv_bt = findViewById(R.id.lv_bt);
//        Set<BluetoothDevice>bt=myBluetoothAdapter.getBondedDevices();
//        String[] strings = new String[bt.size()];
//        int index=0;
//        if(bt.size()>0){
//            for(BluetoothDevice device:bt){
//                strings[index]=(device.getName() + "\n" + device.getAddress());
//                index++;
//            }
//            ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
//            lv_bt.setAdapter(arrayAdapter);
//        }
//        TextView tv_list = findViewById(R.id.tv_list);
//        lv_bt.setVisibility(View.VISIBLE);
//        tv_list.setVisibility(View.VISIBLE);
//        TextView ReadBuffer1 = findViewById(R.id.readBuffer1);
//        ReadBuffer1.setVisibility(View.VISIBLE);
//        TextView connect = findViewById(R.id.connected);
//        connect.setVisibility(View.VISIBLE);
//
//        lv_bt.setOnItemClickListener(DeviceClickListener);
//    }
//
//    private AdapterView.OnItemClickListener DeviceClickListener = new AdapterView.OnItemClickListener() {
//        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//
//            if(!myBluetoothAdapter.isEnabled()) {
//                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Get the device MAC address, which is the last 17 chars in the View
//            String info = ((TextView) v).getText().toString();
//            final String address = info.substring(info.length() - 17);
//            final String name = info.substring(0,info.length() - 17);
//
//            // Spawn a new thread to avoid blocking the GUI one
//            new Thread()
//            {
//                public void run() {
//                    boolean fail = false;
//
//                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);
//
//                    try {
//                        BTSocket = createBluetoothSocket(device);
//                    } catch (IOException e) {
//                        fail = true;
//                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                    }
//                    // Establish the Bluetooth socket connection.
//                    try {
//                        BTSocket.connect();
//                    } catch (IOException e) {
//                        try {
//                            fail = true;
//                            BTSocket.close();
//                            BTHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
//                                    .sendToTarget();
//                        } catch (IOException e2) {
//                            //insert code to deal with this
//                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    if(!fail) {
//                        mConnectedThread = new ConnectedThread(BTSocket);
//                        mConnectedThread.start();
//                        BTHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
//                                .sendToTarget();
//                    }
//                }
//            }.start();
//        }
//    };
//
//    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
//        try {
//            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
//            return (BluetoothSocket) m.invoke(device, PORT_UUID);
//        } catch (Exception e) {
//            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
//        }
//        return  device.createRfcommSocketToServiceRecord(PORT_UUID);
//    }
//
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//            } catch (IOException e) { }
//            mmInStream = tmpIn;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.available();
//                    if(bytes != 0) {
//                        buffer = new byte[1024];
//                        SystemClock.sleep(10); //pause and wait for rest of data. Adjust this depending on your sending speed.
//                        bytes = mmInStream.available(); // how many bytes are ready to be read?
//                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
//                        BTHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                                .sendToTarget(); // Send the obtained bytes to the UI activity
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }
//        }
//    }
//
//    protected void menu() {
//        Button btn_exercises = findViewById(R.id.btn_exercises);
//        Button btn_data = findViewById(R.id.btn_data);
//        Button btn_profile = findViewById(R.id.btn_profile);
//        btn_exercises.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(bluetooth.this, exercises.class));
//            }
//        });
//        btn_data.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(bluetooth.this, data.class));
//            }
//        });
//        btn_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(bluetooth.this, profile.class));
//            }
//        });
//    }
//}
//

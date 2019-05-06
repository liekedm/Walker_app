package com.lademare.walkingaid;

//bluetooth using tutorial: Android Bluetooth Connectivity Tutorial - Sarthi Technology

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class bluetooth extends AppCompatActivity {

    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;
    Intent btEnablingIntent;
    int requestCodeForEnable;
    private final String DEVICE_ADDRESS="a4:50:46:21:c4:fe";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button startButton, sendButton,clearButton,stopButton;
    TextView textView;
    EditText editText;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    boolean setUiEnabled = false;
    boolean ex_1 = false;
    boolean ex_2 = false;
    boolean ex_3 = false;

    //private final String DEVICE_ADDRESS="20:13:10:15:33:66";
    //private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    //private BluetoothDevice device;
    //private BluetoothSocket socket;
    //private InputStream inputStream;

    //boolean deviceConnected=false;
    //byte buffer[];
    //boolean stopThread;

    //int REQUEST_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        overridePendingTransition(0,0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        menu();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_on_off();
    }

    protected void bluetooth_on_off() {
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable=1;
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if (myBluetoothAdapter.isEnabled()){
            btn_btstate.setChecked(true);
            //BTconnect();
        } else {
            btn_btstate.setChecked(false);
            TextView tv_list = findViewById(R.id.tv_list);
            tv_list.setVisibility(View.INVISIBLE);
        }
        btn_btstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter==null){
                    Toast.makeText(getApplicationContext(),"Bluetooth not supported", Toast.LENGTH_LONG).show();
                } else{
                    if (!myBluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
                        //BTconnect();
                    }
                }
                if (myBluetoothAdapter.isEnabled()){
                    myBluetoothAdapter.disable();
                    TextView tv_list = findViewById(R.id.tv_list);
                    ListView lv_bt = findViewById(R.id.lv_bt);
                    tv_list.setVisibility(View.INVISIBLE);
                    lv_bt.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if(requestCode==requestCodeForEnable){
            if(resultCode==RESULT_OK){
                bluetooth_list();
            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Bluetooth enabling cancelled",Toast.LENGTH_LONG).show();
                btn_btstate.setChecked(false);
            }
        }
    }

   protected void bluetooth_list() {
        ListView lv_bt = findViewById(R.id.lv_bt);
        Set<BluetoothDevice>bt=myBluetoothAdapter.getBondedDevices();
        String[] strings = new String[bt.size()];
        int index=0;
        if(bt.size()>0){
            for(BluetoothDevice device:bt){
                strings[index]=device.getName();
                index++;
            }
            ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
            lv_bt.setAdapter(arrayAdapter);
        }
        TextView tv_list = findViewById(R.id.tv_list);
        lv_bt.setVisibility(View.VISIBLE);
        tv_list.setVisibility(View.VISIBLE);
//
////        for (BluetoothDevice iterator : bt)
////        {
////            if(iterator.getAddress().equals(DEVICE_ADDRESS))
////            {
////                device=iterator;
////                break;
////            }
////        }
//        lv_bt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ClientClass clientClass = new ClientClass(btArray[i]);
//            }
//        });
    }

//    public boolean BTconnect() {
//        boolean connected=true;
//        try {
//            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
//            socket.connect();
//        } catch (IOException e) {
//            e.printStackTrace();
//            connected=false;
//        }
//        if(connected)
//        {
//            try {
//                outputStream=socket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                inputStream=socket.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        beginListenForData();
//        return connected;
//    }
//
//    void beginListenForData()
//    {
//        final Handler handler = new Handler();
//        stopThread = false;
//        buffer = new byte[1024];
//        Thread thread  = new Thread(new Runnable()
//        {
//            public void run()
//            {
//                while(!Thread.currentThread().isInterrupted() && !stopThread)
//                {
//                    try
//                    {
//                        int byteCount = inputStream.available();
//                        if(byteCount > 0)
//                        {
//                            byte[] rawBytes = new byte[byteCount];
//                            inputStream.read(rawBytes);
//                            final String string=new String(rawBytes,"UTF-8");
//                            handler.post(new Runnable() {
//                                public void run()
//                                {
//                                    TextView textView= findViewById(R.id.textView);
//                                    textView.append(string);
//                                }
//                            });
//                        }
//                    }
//                    catch (IOException ex)
//                    {
//                        stopThread = true;
//                    }
//                }
//            }
//        });
//
//        thread.start();
//    }

    protected void menu() {
        Button btn_exercises = findViewById(R.id.btn_exercises);
        Button btn_data = findViewById(R.id.btn_data);
        Button btn_profile = findViewById(R.id.btn_profile);
        btn_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bluetooth.this, exercises.class));
            }
        });
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bluetooth.this, data.class));
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bluetooth.this, profile.class));
            }
        });
    }
}

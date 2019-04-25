package com.lademare.walkingaid;

//bluetooth using tutorial: Android Bluetooth Connectivity Tutorial - Sarthi Technology

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;

public class bluetooth extends AppCompatActivity {

    BluetoothAdapter myBluetoothAdapter;
    Intent btEnablingIntent;
    int requestCodeForEnable;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter myAdapter = BluetoothAdapter.getDefaultAdapter();

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
           ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
           lv_bt.setAdapter(arrayAdapter);
        }
        TextView tv_list = findViewById(R.id.tv_list);
        lv_bt.setVisibility(View.VISIBLE);
        tv_list.setVisibility(View.VISIBLE);
    }

    protected void bluetooth_list_new() {
        ListView lv_bt_new = findViewById(R.id.lv_bt_new);
        myAdapter.startDiscovery();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReciever,intentFilter);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
        lv_bt_new.setAdapter(arrayAdapter);
        TextView tv_list_new = findViewById(R.id.tv_list_new);
        lv_bt_new.setVisibility(View.VISIBLE);
        tv_list_new.setVisibility(View.VISIBLE);
    }

    BroadcastReceiver myReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    protected void bluetooth_on_off() {
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable=1;
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if (myBluetoothAdapter.isEnabled()){
            btn_btstate.setChecked(true);
            bluetooth_list();
            bluetooth_list_new();
        } else {
            btn_btstate.setChecked(false);
            TextView tv_list = findViewById(R.id.tv_list);
            TextView tv_list_new = findViewById(R.id.tv_list_new);
            tv_list.setVisibility(View.INVISIBLE);
            tv_list_new.setVisibility(View.INVISIBLE);
        }
        btn_btstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter==null){
                    Toast.makeText(getApplicationContext(),"Bluetooth not supported", Toast.LENGTH_LONG).show();
                } else{
                    if (!myBluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
                    }
                }
                if (myBluetoothAdapter.isEnabled()){
                    myBluetoothAdapter.disable();
                    TextView tv_list = findViewById(R.id.tv_list);
                    ListView lv_bt = findViewById(R.id.lv_bt);
                    TextView tv_list_new = findViewById(R.id.tv_list_new);
                    ListView lv_bt_new = findViewById(R.id.lv_bt_new);
                    tv_list.setVisibility(View.INVISIBLE);
                    lv_bt.setVisibility(View.INVISIBLE);
                    lv_bt_new.setVisibility(View.INVISIBLE);
                    tv_list_new.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if(requestCode==requestCodeForEnable){
            if(resultCode==RESULT_OK){
                bluetooth_list();
                bluetooth_list_new();
            }
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Bluetooth enabling cancelled",Toast.LENGTH_LONG).show();
                btn_btstate.setChecked(false);
            }
        }
    }

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

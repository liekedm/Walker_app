package com.lademare.walkingaid;

//bluetooth using tutorial: Android Bluetooth Connectivity Tutorial - Sarthi Technology

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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

import java.util.Set;

public class bluetooth extends AppCompatActivity {

    BluetoothAdapter myBluetoothAdapter;
    Intent btEnablingIntent;
    int requestCodeForEnable;

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
        bluetooth_list();
    }

    protected void bluetooth_list() {
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
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
            //listView.setAdapter(arrayAdapter);
        }
    }

    protected void bluetooth_on_off() {
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable=1;
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if (myBluetoothAdapter.isEnabled()){
            btn_btstate.setChecked(true);
        } else {
            btn_btstate.setChecked(false);
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
                    Toast.makeText(getApplicationContext(),"Bluetooth is disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ToggleButton btn_btstate = findViewById(R.id.btn_btstate);
        if(requestCode==requestCodeForEnable){
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"Bluetooth is enabled",Toast.LENGTH_LONG).show();
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

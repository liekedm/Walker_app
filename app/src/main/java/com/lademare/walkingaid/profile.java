package com.lademare.walkingaid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class profile extends AppCompatActivity{

    EditText editWeight;
    EditText editWeightbearing;
    EditText editMessege;

    public static final String weight = "weight";
    public static final String weightbearing = "weightbearing";
    public static final String messege = "messege";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        editWeight = findViewById(R.id.editWeight);
        editWeightbearing = findViewById(R.id.editWeightbearing);
        editMessege = findViewById(R.id.editMessege);
        Spinner spinner = findViewById(R.id.editLimb);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.limb_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        spinner.setSelection(sp.getInt("position",0));
        spinner.setAdapter(adapter);

        menu();
        getdata();
    }

    protected void menu() {
        Button btn_exercises = findViewById(R.id.btn_exercises);
        Button btn_data = findViewById(R.id.btn_data);
        TextView btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setTextColor(Color.WHITE);
        btn_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, exercises.class));
                editdata();
            }
        });
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, data.class));
                editdata();
            }
        });
        ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, bluetooth.class));
            }
        });
    }

    protected void getdata() {
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        editWeight.setText(String.valueOf(sp.getInt(weight, 80)));
        editWeightbearing.setText(String.valueOf(sp.getInt(weightbearing, 80)));
        editMessege.setText(sp.getString(messege, " "));
    }

    protected void editdata() {
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        final Spinner spinner = findViewById(R.id.editLimb);
        editor.putInt("position", spinner.getSelectedItemPosition());
        String spinnerlimb = spinner.getSelectedItem().toString();
        int weight = Integer.parseInt(editWeight.getText().toString());
        int weightbearing = Integer.parseInt(editWeightbearing.getText().toString());
        String messege = editMessege.getText().toString();
        editor.putString("limb", spinnerlimb);
        editor.putInt("weight", weight);
        editor.putInt("weightbearing", weightbearing);
        editor.putString("messege", messege);
        editor.apply();
    }

}

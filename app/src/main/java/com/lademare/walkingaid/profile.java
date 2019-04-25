package com.lademare.walkingaid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class profile extends AppCompatActivity{

    EditText editWeight;
    EditText editWeightbearing;
    EditText editMessege;

    public static final String limb = "limb";
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
        ToggleButton left_right = findViewById(R.id.left_right);
        if ((sp.getString(limb, "Left").equals("Right"))) {
            left_right.setChecked(true);
        } else {
            left_right.setChecked(false);
        }
        editWeight.setText(String.valueOf(sp.getInt(weight, 80)));
        editWeightbearing.setText(String.valueOf(sp.getInt(weightbearing, 80)));
        editMessege.setText(sp.getString(messege, " "));
    }

    protected void editdata() {
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        ToggleButton left_right = findViewById(R.id.left_right);
        if (left_right.isChecked()) {
            editor.putString("limb", "Right");
        } else {
            editor.putString("limb", "Left");
        }
        int weight = Integer.parseInt(editWeight.getText().toString());
        int weightbearing = Integer.parseInt(editWeightbearing.getText().toString());
        String messege = editMessege.getText().toString();
        editor.putInt("weight", weight);
        editor.putInt("weightbearing", weightbearing);
        editor.putString("messege", messege);
        editor.apply();
    }

}

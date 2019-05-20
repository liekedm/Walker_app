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
import android.widget.ToggleButton;

public class profile extends AppCompatActivity{

    EditText edittherapistname;
    EditText editMessege;

    public static final String limb = "limb";
    public static final String walkingaid = "walkingaid";
    public static final String nametherapist = "nametherapist";
    public static final String messege = "messege";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        edittherapistname = findViewById(R.id.edittherapistname);
        editMessege = findViewById(R.id.editMessege);

        menu();
        getdata();
    }

    protected void getdata() {
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        ToggleButton left_right = findViewById(R.id.left_right);
        if ((sp.getString(limb, "Left").equals("Right"))) {
            left_right.setChecked(true);
        } else {
            left_right.setChecked(false);
        }
        ToggleButton crutch_stick = findViewById(R.id.crutch_stick);
        if ((sp.getString(walkingaid, "Crutch").equals("Stick"))) {
            crutch_stick.setChecked(true);
        } else {
            crutch_stick.setChecked(false);
        }
        edittherapistname.setText(String.valueOf(sp.getString(nametherapist, " ")));
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
        ToggleButton crutch_stick = findViewById(R.id.crutch_stick);
        if (crutch_stick.isChecked()) {
            editor.putString("walkingaid", "Stick");
        } else {
            editor.putString("walkingaid", "Crutch");
        }
        String nametherapist = edittherapistname.getText().toString();
        String messege = editMessege.getText().toString();
        editor.putString("nametherapist", nametherapist);
        editor.putString("messege", messege);
        editor.apply();
    }

    protected void menu() {
        Button btn_exercises = findViewById(R.id.btn_exercises);
        Button btn_data = findViewById(R.id.btn_data);
        TextView btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setTextColor(Color.WHITE);
        btn_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editdata();
                startActivity(new Intent(profile.this, exercises.class));
            }
        });
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editdata();
                startActivity(new Intent(profile.this, data.class));
            }
        });
    }

}

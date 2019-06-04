package com.lademare.walkingaid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class profiletp1 extends AppCompatActivity{

    EditText editMessege;
    ToggleButton btn_datashow;
    ToggleButton btn_visual;
    ToggleButton btn_graph;
    ToggleButton btn_voice;
    ToggleButton btn_tones;
    ToggleButton btn_vibrations;

    public static final String limb = "limb";
    public static final String walkingaid = "walkingaid";
    public static final String messege = "messege";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_profile_tp1);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        editMessege = findViewById(R.id.editMessege);

        menu();
        getdata();
        buttons();
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
        String messege = editMessege.getText().toString();
        editor.putString("messege", messege);
        editor.apply();
    }

    protected void buttons(){
        btn_datashow = findViewById(R.id.btn_datashow);
        btn_visual = findViewById(R.id.btn_visual);
        btn_voice = findViewById(R.id.btn_voice);
        btn_tones = findViewById(R.id.btn_tones);
        btn_vibrations = findViewById(R.id.btn_vibrations);
        btn_datashow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_datashow.isChecked()){
                    btn_visual.setVisibility(View.VISIBLE);
                } else {
                    btn_visual.setVisibility(View.INVISIBLE);
                }
            }
        });
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(btn_voice.isChecked())){
                    btn_voice.setPaintFlags(btn_voice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    btn_voice.setPaintFlags(btn_voice.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
        btn_tones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(btn_tones.isChecked())){
                    btn_tones.setPaintFlags(btn_tones.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    btn_tones.setPaintFlags(btn_tones.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
        btn_vibrations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(btn_vibrations.isChecked())){
                    btn_vibrations.setPaintFlags(btn_vibrations.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    btn_vibrations.setPaintFlags(btn_vibrations.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }
    protected void menu() {
        Button btn_data = findViewById(R.id.btn_data);
        TextView btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setTextColor(Color.WHITE);
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editdata();
                startActivity(new Intent(profiletp1.this, datatp1.class));
            }
        });
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profiletp1.this, choosepatient.class));
            }
        });
    }

}

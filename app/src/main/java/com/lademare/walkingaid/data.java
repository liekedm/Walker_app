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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class data extends AppCompatActivity {

    public static final String limb = "limb";
    public static final String weight = "weight";
    public static final String weightbearing = "weightbearing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_data);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        menu();

        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        ImageView iv_left = findViewById(R.id.iv_left);
        ImageView iv_right = findViewById(R.id.iv_right);
        final Spinner spinner = findViewById(R.id.editLimb);
        if ((sp.getString(limb, "Left").equals("Right"))) {
            iv_left.setVisibility(View.INVISIBLE);
            iv_right.setVisibility(View.VISIBLE);
        } else {
            iv_left.setVisibility(View.VISIBLE);
            iv_right.setVisibility(View.INVISIBLE);
        }
    }

    protected void menu() {
        Button btn_exercises = findViewById(R.id.btn_exercises);
        Button btn_profile = findViewById(R.id.btn_profile);
        TextView btn_data = findViewById(R.id.btn_data);
        btn_data.setTextColor(Color.WHITE);
        btn_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(data.this, exercises.class));
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(data.this, profile.class));
            }
        });
        ImageButton btn_bluetooth = findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(data.this, bluetooth.class));
            }
        });
    }
}

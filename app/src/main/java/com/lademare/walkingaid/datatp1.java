package com.lademare.walkingaid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class datatp1 extends AppCompatActivity {

    boolean lm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_data_tp1);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        menu();

        String language;
        String languagesave = "languagesave";
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        if ((sp.getString(languagesave, "en").equals("nl"))) {
            language = "nl";
        } else {
            language = "en";
        }
        ImageView GraphComplete1 = findViewById(R.id.GraphComplete1);
        if (language.equals("nl")){
            GraphComplete1.setImageResource(R.drawable.graphcomplete1_tp_nl);
        } else {
            GraphComplete1.setImageResource(R.drawable.graphcomplete1_tp);
        }
        ImageView GraphComplete2 = findViewById(R.id.GraphComplete2);
        if (language.equals("nl")){
            GraphComplete2.setImageResource(R.drawable.graphcomplete2_tp_nl);
        } else {
            GraphComplete2.setImageResource(R.drawable.graphcomplete2_tp);
        }
    }

    protected void menu() {
        Button btn_profile = findViewById(R.id.btn_profile);
        TextView btn_data = findViewById(R.id.btn_data);
        btn_data.setTextColor(Color.WHITE);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(datatp1.this, profiletp1.class));
            }
        });
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(datatp1.this, choosepatient.class));
            }
        });
    }
}

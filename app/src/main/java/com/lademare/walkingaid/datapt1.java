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
import android.widget.ImageView;
import android.widget.TextView;

public class datapt1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_data_pt1);
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
        ImageView GraphSimple1 = findViewById(R.id.GraphSimple1);
        if (language.equals("nl")){
            GraphSimple1.setImageResource(R.drawable.graphsimple1_pt_nl);
        } else {
            GraphSimple1.setImageResource(R.drawable.graphsimple1_pt);
        }
        ImageView GraphSimple2 = findViewById(R.id.GraphSimple2);
        if (language.equals("nl")){
            GraphSimple2.setImageResource(R.drawable.graphsimple2_pt_nl);
        } else {
            GraphSimple2.setImageResource(R.drawable.graphsimple2_pt);
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
                startActivity(new Intent(datapt1.this, exercisespt1.class));
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(datapt1.this, profilept1.class));
            }
        });
    }
}

package com.lademare.walkingaid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class login extends AppCompatActivity {

    ToggleButton btn_language;
    String language;
    public static final String languagesave = "languagesave";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(0,0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        btn_language = findViewById(R.id.btn_language);
        SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
        if ((sp.getString(languagesave, "nl").equals("nl"))) {
            btn_language.setChecked(false);
        } else {
            btn_language.setChecked(true);
        }
        btn_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("sharedprefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if ((btn_language.isChecked())){
                    language = "en";
                    editor.putString("languagesave", "en");
                } else {
                    language = "nl";
                    editor.putString("languagesave", "nl");
                }
                editor.apply();
                setLocale();
            }
        });

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editName = findViewById(R.id.editName);
                String name = editName.getText().toString();
                EditText editPassword = findViewById(R.id.editPassword);
                String password = editPassword.getText().toString();
                if (name.equals("david") && password.equals("david")){
                    startActivity(new Intent(login.this, exercisespt1.class));
                } else if (name.equals("sonja") && password.equals("sonja")){
                    startActivity(new Intent(login.this, exercisespt2.class));
                } else if (name.equals("kate") && password.equals("kate")){
                startActivity(new Intent(login.this, choosepatient.class));
                } else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_loginfailed),Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    public void setLocale() {
        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, login.class);
        finish();
        startActivity(refresh);
    }

}

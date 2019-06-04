package com.lademare.walkingaid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(0,0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

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
                    Toast.makeText(getApplicationContext(),"Invalid name or password",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}

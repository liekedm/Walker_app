package com.lademare.walkingaid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class getdata extends AppCompatActivity {

    String fileName = "logdata.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getdata);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        Context context = getApplicationContext();
        String fileData = readFromFile(context, fileName);
        TextView datalog = findViewById(R.id.datalog);
        datalog.setText(fileData);
    }

    private String readFromFile(Context context, String fileName) {
        String data = " ";
        try {
            InputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    //Toast.makeText(this, "Data received", Toast.LENGTH_SHORT).show();
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                String incomming = stringBuilder.toString();
                data = incomming.replaceAll("n.l.", System.getProperty ("line.separator"));
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Can not read file", Toast.LENGTH_SHORT).show();
        }
        return data;
    }
}

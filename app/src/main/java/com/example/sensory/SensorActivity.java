package com.example.sensory;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class SensorActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //serves the main purpose of this application
        new SensorController(this);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}
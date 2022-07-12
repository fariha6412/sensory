package com.example.sensory;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity{
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //first we need to check for the necessary permissions to continue the application
        //if not permitted, we will start the mainActivity instead of our service
        boolean isPermitted = isPermissionGranted();
        if(!isPermitted)requestPermission();
        isPermitted = isPermissionGranted();
        if(!isPermitted)setContentView(R.layout.activity_main);
        else {
            if (!isForegroundServiceRunning(this)) {
                Intent intent = new Intent(this, MyService.class);
                startForegroundService(intent);
            } else startActivity(new Intent(this, SensorActivity.class));
        }
    }
    public static boolean isForegroundServiceRunning(Context context){
        /*---------checks if the service is still running or not ---------*/

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(MyService.class.getName().equals(service.service.getClassName()))return true;
        }
        return false;
    }

    public boolean isPermissionGranted(){
        /*------ checks if necessary permissions are granted or not. Returns a boolean value---------*/

        boolean isPermitted = false;
        isPermitted = (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED);
        isPermitted |= (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED);
        isPermitted |= (this.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED);
        isPermitted |= (this.checkSelfPermission(Manifest.permission.EXPAND_STATUS_BAR) == PackageManager.PERMISSION_DENIED);
        isPermitted |= (this.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED);

        return isPermitted;
    }


    public void requestPermission(){
        /*----------requests for necessary permissions if not granted yet--------*/

        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (this.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 0);
        }
        if (this.checkSelfPermission(Manifest.permission.EXPAND_STATUS_BAR) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.EXPAND_STATUS_BAR}, 0);
        }
        if (this.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.FOREGROUND_SERVICE}, 0);
        }
    }
}
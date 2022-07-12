package com.example.sensory;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyService extends Service{

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        startForeground();

        Intent it = new Intent(getApplicationContext(), SensorActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground(){
        /*-------- notifies the user that service is still running for security purpose ---------*/

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, SensorActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "foreground_service", NotificationManager.IMPORTANCE_NONE);
        notificationManager.createNotificationChannel(notificationChannel);

        startForeground(1001, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentText("Service is running.")
        .setContentIntent(pendingIntent)
        .build());

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

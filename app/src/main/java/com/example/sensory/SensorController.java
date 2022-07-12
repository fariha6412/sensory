package com.example.sensory;

import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SensorController implements SensorEventListener {

    ArrayList<AdapterModel> adapterModels = new ArrayList<>();
    SensorAdapter sensorAdapter;

    DBHelper dbHelper;
    int dbEntryCount = 0, dbEntryCapacity = 24; //dbEntryCapacity is the highest number of entry the db can hold upto
                                                //it will delete the earliest entry when
                                                //it needs to insert a new one
                                                //dbEntryCount will count the entry as well as work as the primary key
    Timer timer = new Timer();                  //for inserting new data when a timer of 5 min is over
    int minutePassed = 0, dbEntryDelay = 5;
    boolean dbFull = false;

    //variables for keeping track of the sensor values
    //will be updated when a relevant event occurs
    float lastValueLightSensor, lastValueProximitySensor;
    float[] lastValueGyroscopeSensor = new float[3];
    float[] lastValueAccelerometerSensor = new float[3];

    //sensors
    SensorManager sensorManager;
    Sensor lightSensor, gyroSensor, proximitySensor, accelerometerSensor;

    Activity activity;

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SensorController(Activity activity) {
        this.activity = activity;

        sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);

        //initializing all the sensors
        if(sensorManager!=null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(lightSensor!=null){
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else Toast.makeText(activity, "Light sensor not detected", Toast.LENGTH_SHORT).show();

            if(gyroSensor!=null)sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
            else Toast.makeText(activity, "Gyroscope sensor not detected", Toast.LENGTH_SHORT).show();

            if(proximitySensor!=null)sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            else Toast.makeText(activity, "Proximity sensor not detected", Toast.LENGTH_SHORT).show();

            if(accelerometerSensor!=null)sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            else Toast.makeText(activity, "Accelerometer sensor not detected", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(activity, "Sensor not detected", Toast.LENGTH_SHORT).show();

        setUpSensorAdapter();
        setUpDBHelper();
        makeStickyNotification();
    }


    //runnable for updating the database when the timer is up
    Runnable update = () -> {
        minutePassed++;
        //System.out.println("minutePassed: "+ minutePassed);

        int closeFlag = 0;  //it means no db closing requires in dbHelper functions, here we will do it at the last
        if(minutePassed == dbEntryDelay){
            dbEntryCount++;
            if(dbEntryCount > dbEntryCapacity){
                dbFull = true;
                dbEntryCount = 1;
            }
            dbHelper.makeWritableDatabase();
            try {

                //if the db is full, delete the earliest one to make room for the newest one
                if (dbFull) {
                    dbHelper.deleteSingleLightSensorEntry(dbEntryCount, closeFlag);
                    dbHelper.deleteSingleProximitySensorEntry(dbEntryCount, closeFlag);
                    dbHelper.deleteSingleGyroscopeSensorEntry(dbEntryCount, closeFlag);
                    dbHelper.deleteSingleAccelerometerSensorEntry(dbEntryCount, closeFlag);
                }
                dbHelper.addLightSensorData(dbEntryCount, lastValueLightSensor, closeFlag);
                dbHelper.addProximitySensorData(dbEntryCount, lastValueProximitySensor, closeFlag);
                dbHelper.addGyroscopeSensorData(dbEntryCount, lastValueGyroscopeSensor, closeFlag);
                dbHelper.addAccelerometerSensorData(dbEntryCount, lastValueAccelerometerSensor, closeFlag);
            } catch (SQLiteConstraintException e){
                e.printStackTrace();
            }
            finally {
                dbHelper.closeWritableDatabase();
                //restarting timer
                minutePassed = 0;
            }
        }
    };


    //keeps notifying the user of the all 4 values of the sensor
    Runnable notify = () -> {
        builder.setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Light Sensor: " + lastValueLightSensor)
                        .addLine("Proximity Sensor: " + lastValueProximitySensor)
                        .addLine("Gyroscope:")
                        .addLine("X: " + (lastValueGyroscopeSensor[0]) + " \n" + "Y: " + (lastValueGyroscopeSensor[1]) + " \n" + "Z: " + (lastValueGyroscopeSensor[2]))
                        .addLine("Accelerometer:")
                        .addLine("X: " + (lastValueAccelerometerSensor[0]) + " \n" + "Y: " + (lastValueAccelerometerSensor[1]) + " \n" + "Z: " + (lastValueAccelerometerSensor[2]))
                );

        notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    };


    private void setUpTimer(){
        int millisInAMinute = 60000;
        long time = System.currentTimeMillis();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update.run();
            }
        }, time % millisInAMinute, millisInAMinute);
    }

    void setUpDBHelper(){
        dbHelper = new DBHelper(activity);
        setUpTimer();
    }

    @SuppressLint("NotifyDataSetChanged")
    void setUpSensorAdapter(){
        AdapterModel lightSensorAdapterModel = new AdapterModel("Light Sensor", "value", R.drawable.light_white);
        AdapterModel gyroscopeSensorAdapterModel = new AdapterModel("Gyroscope", "value", R.drawable.gyro_white);
        AdapterModel proximitySensorAdapterModel = new AdapterModel("Proximity Sensor", "value", R.drawable.proxi_white);
        AdapterModel accelerometerSensorAdapterModel = new AdapterModel("Accelerometer", "value", R.drawable.accele_white);

        adapterModels.add(lightSensorAdapterModel);
        adapterModels.add(gyroscopeSensorAdapterModel);
        adapterModels.add(proximitySensorAdapterModel);
        adapterModels.add(accelerometerSensorAdapterModel);

        RecyclerView recyclerView = activity.findViewById(R.id.recyclerView);
        sensorAdapter = new SensorAdapter(adapterModels);

        recyclerView.setLayoutManager(new GridLayoutManager(this.activity, 2));
        recyclerView.setAdapter(sensorAdapter);
        sensorAdapter.setOnItemClickListener(position -> System.out.println(adapterModels.get(position).getTitle()));
        sensorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            adapterModels.get(0).setValue(Float.toString(sensorEvent.values[0]));
            lastValueLightSensor = sensorEvent.values[0];
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            adapterModels.get(1).setValue("X: " + (sensorEvent.values[0]) + " \n" + "Y: " + (sensorEvent.values[1]) + " \n" + "Z: " + (sensorEvent.values[2]));
            lastValueGyroscopeSensor = sensorEvent.values;
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
            adapterModels.get(2).setValue(Float.toString(sensorEvent.values[0]));
            lastValueProximitySensor = sensorEvent.values[0];
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            adapterModels.get(3).setValue("X: " + (sensorEvent.values[0]) + " \n" + "Y: " + (sensorEvent.values[1]) + " \n" + "Z: " + (sensorEvent.values[2]));
            lastValueAccelerometerSensor = sensorEvent.values;
        }
        sensorAdapter.setModels(adapterModels);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeStickyNotification() {
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "sensor_values", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Sensor Values")
                .setContentInfo("Info")
                .setOnlyAlertOnce(true);

        Thread thread = new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000);
                    notify.run();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}

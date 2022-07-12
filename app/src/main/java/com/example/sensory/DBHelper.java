package com.example.sensory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SensorManager";
    private static final String TABLE_LIGHT_SENSOR = "LightSensor";
    private static final String TABLE_PROXIMITY_SENSOR = "ProximitySensor";
    private static final String TABLE_GYROSCOPE_SENSOR = "GyroscopeSensor";
    private static final String TABLE_ACCELEROMETER_SENSOR = "AccelerometerSensor";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_VALUE = "value";
    private static final String KEY_VALUE_X = "valueX";
    private static final String KEY_VALUE_Y = "valueY";
    private static final String KEY_VALUE_Z = "valueZ";

    private static SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //onUpgrade(sqLiteDatabase, 1, 2);

        String CREATE_TABLE_LIGHT_SENSOR = "CREATE TABLE " + TABLE_LIGHT_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + ", "+ KEY_TIME + " TEXT " + ", " + KEY_VALUE + " TEXT " + ")";
        String CREATE_TABLE_GYROSCOPE_SENSOR = "CREATE TABLE " + TABLE_GYROSCOPE_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + ", "+ KEY_TIME + " TEXT " + "," + KEY_VALUE_X + " TEXT " + "," + KEY_VALUE_Y + " TEXT " + "," + KEY_VALUE_Z + " TEXT " + ")";
        String CREATE_TABLE_PROXIMITY_SENSOR = "CREATE TABLE " + TABLE_PROXIMITY_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + ", "+ KEY_TIME + " TEXT " + ", " + KEY_VALUE + " TEXT " + ")";
        String CREATE_TABLE_ACCELEROMETER_SENSOR = "CREATE TABLE " + TABLE_ACCELEROMETER_SENSOR + "("
                + KEY_ID + " INTEGER PRIMARY KEY " + ", "+ KEY_TIME + " TEXT " + "," + KEY_VALUE_X + " TEXT " + "," + KEY_VALUE_Y + " TEXT " + "," + KEY_VALUE_Z + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_LIGHT_SENSOR);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROXIMITY_SENSOR);
        sqLiteDatabase.execSQL(CREATE_TABLE_GYROSCOPE_SENSOR);
        sqLiteDatabase.execSQL(CREATE_TABLE_ACCELEROMETER_SENSOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGHT_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PROXIMITY_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GYROSCOPE_SENSOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCELEROMETER_SENSOR);

        onCreate(sqLiteDatabase);
    }

    public void makeWritableDatabase(){
        sqLiteDatabase = this.getWritableDatabase();
    }
    public void closeWritableDatabase(){
        sqLiteDatabase.close();
    }
    private Map<String, String> getTableData(String table_name, int closeFlag){
        Map<String, String> map = new HashMap<>();
        String selectQuery = "SELECT *FROM " + table_name;
        if(!sqLiteDatabase.isOpen())sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                String time, value;
                time = cursor.getString(1);
                value = cursor.getString(2);
                map.put(time, value);
            }while(cursor.moveToNext());
        }
        cursor.close();
        if(closeFlag == 1)sqLiteDatabase.close();
        return map;
    }

    private Map<String, String[]> getTableDataValue3(String table_name, int closeFlag){
        Map<String, String[]> map = new HashMap<>();
        String selectQuery = "SELECT *FROM " + table_name;
        if(!sqLiteDatabase.isOpen())sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                String time;
                String[] value = new String[3];
                time = cursor.getString(1);
                value[0] = cursor.getString(2);
                value[1] = cursor.getString(3);
                value[2] = cursor.getString(4);
                map.put(time, value);
            }while(cursor.moveToNext());
        }
        cursor.close();
        if(closeFlag == 1)sqLiteDatabase.close();
        return map;
    }

    private void insertIntoTable(String table_name, int id, Float value, int closeFlag){
        if(!sqLiteDatabase.isOpen())sqLiteDatabase = this.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String str = date.toString();
        String time = str.split("\\s")[3];

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_TIME, time);
        values.put(KEY_VALUE, Float.toString(value));

        sqLiteDatabase.insert(table_name, null, values);
        if(closeFlag == 1)sqLiteDatabase.close();
    }

    private void insertIntoTable(String table_name, int id, float[] value, int closeFlag){
        if(!sqLiteDatabase.isOpen())sqLiteDatabase = this.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String str = date.toString();
        String time = str.split("\\s")[3];

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_TIME, time);
        values.put(KEY_VALUE_X, Float.toString(value[0]));
        values.put(KEY_VALUE_Y, Float.toString(value[1]));
        values.put(KEY_VALUE_Z, Float.toString(value[2]));

        sqLiteDatabase.insert(table_name, null, values);
        if(closeFlag == 1)sqLiteDatabase.close();
    }

    private void deleteSingleTableRow(String table_name, int id, int closeFlag){
        if(!sqLiteDatabase.isOpen())sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(table_name, KEY_ID + "=?", new String[] {String.valueOf(id)});
        if(closeFlag == 1)sqLiteDatabase.close();
    }

    public void addLightSensorData(int id, float value, int closeFlag){
        insertIntoTable(TABLE_LIGHT_SENSOR,id, value, closeFlag);
    }

    public Map<String, String> getAllLightSensorEntry(int closeFlag){
        return getTableData(TABLE_LIGHT_SENSOR, closeFlag);
    }

    public void deleteSingleLightSensorEntry(int id, int closeFlag){
        deleteSingleTableRow(TABLE_LIGHT_SENSOR, id, closeFlag);
    }

    public void addProximitySensorData(int id, float value, int closeFlag){
        insertIntoTable(TABLE_PROXIMITY_SENSOR, id, value, closeFlag);
    }

    public Map<String, String> getAllProximitySensorEntry(int closeFlag){
        return getTableData(TABLE_PROXIMITY_SENSOR, closeFlag);
    }

    public void deleteSingleProximitySensorEntry(int id, int closeFlag){
        deleteSingleTableRow(TABLE_PROXIMITY_SENSOR, id, closeFlag);
    }

    public void addGyroscopeSensorData(int id, float[] value, int closeFlag){
        insertIntoTable(TABLE_GYROSCOPE_SENSOR, id, value, closeFlag);
    }

    public Map<String, String[]> getAllGyroscopeSensorEntry(int closeFlag){
        return getTableDataValue3(TABLE_GYROSCOPE_SENSOR, closeFlag);
    }

    public void deleteSingleGyroscopeSensorEntry(int id, int closeFlag){
        deleteSingleTableRow(TABLE_GYROSCOPE_SENSOR, id, closeFlag);
    }

    public void addAccelerometerSensorData(int id, float[] value, int closeFlag){
        insertIntoTable(TABLE_ACCELEROMETER_SENSOR, id, value, closeFlag);
    }

    public Map<String, String[]> getAllAccelerometerSensorEntry(int closeFlag){
        return getTableDataValue3(TABLE_ACCELEROMETER_SENSOR, closeFlag);
    }
    public void deleteSingleAccelerometerSensorEntry(int id, int closeFlag){
        deleteSingleTableRow(TABLE_ACCELEROMETER_SENSOR, id, closeFlag);
    }
}

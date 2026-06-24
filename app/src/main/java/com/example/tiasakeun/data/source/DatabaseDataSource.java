package com.example.tiasakeun.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tiasakeun.data.local.ActivityContract;
import com.example.tiasakeun.data.local.DbHelper;
import com.example.tiasakeun.data.local.ScheduleContract;
import com.example.tiasakeun.data.local.TypeContract;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.Type;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDataSource {
    private SQLiteDatabase database;
    private final DbHelper dbHelper;

    public DatabaseDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    //Buka tutup koneksi database
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //CREATE
    public long createActivity(long userId, long typeId, long scheduleId, String title, int currentValue, int targetValue, int notification, int imageResource) {
        ContentValues values = new ContentValues();
        values.put(ActivityContract.ActivityEntry.COLUMN_TITLE, title);
        values.put(ActivityContract.ActivityEntry.COLUMN_USER_ID, userId);
        values.put(ActivityContract.ActivityEntry.COLUMN_TYPE_ID, typeId);
        values.put(ActivityContract.ActivityEntry.COLUMN_SCHEDULE_ID, scheduleId);
        values.put(ActivityContract.ActivityEntry.COLUMN_CURRENT_VALUE, currentValue);
        values.put(ActivityContract.ActivityEntry.COLUMN_TARGET_VALUE, targetValue);
        values.put(ActivityContract.ActivityEntry.COLUMN_NOTIFICATION, notification);
        values.put(ActivityContract.ActivityEntry.COLUMN_IMAGE_RESOURCE, imageResource);
        return database.insert(ActivityContract.ActivityEntry.TABLE_NAME, null, values);
    }

    //READ

    public ArrayList<Activity> getAllActivities() {
        ArrayList<Activity> activities = new ArrayList<>();
        String query = "Select _id, title, current_value, target_value, image_resource FROM " + ActivityContract.ActivityEntry.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                activities.add(new Activity(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  activities;
    }
    public ArrayList<Type> getAllTypes() {
        ArrayList<Type> types = new ArrayList<>();
        String query = "SELECT _id, category, unit_name FROM " + TypeContract.TypeEntry.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                types.add(new Type(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return types;
    }

    public ArrayList<Schedule> getAllSchedules() {
        ArrayList<Schedule> schedules = new ArrayList<>();
        String query = "Select _id, type FROM " + ScheduleContract.ScheduleEntry.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                schedules.add(new Schedule(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return schedules;
    }
    //UPDATE
    //DELETE
}

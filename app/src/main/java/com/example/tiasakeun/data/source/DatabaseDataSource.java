package com.example.tiasakeun.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tiasakeun.data.local.ActivityContract.ActivityEntry;
import com.example.tiasakeun.data.local.SubActivityContract.SubActivityEntry;
import com.example.tiasakeun.data.local.ActivityLogContract.ActivityLogEntry;
import com.example.tiasakeun.data.local.DbHelper;
import com.example.tiasakeun.data.local.ScheduleContract.ScheduleEntry;
import com.example.tiasakeun.data.local.TypeContract.TypeEntry;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.model.Type;

import java.util.ArrayList;

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
    public long createActivity(long userId, long typeId, String title, int imageResource) {
        ContentValues values = new ContentValues();
        values.put(ActivityEntry.COLUMN_USER_ID, userId);
        values.put(ActivityEntry.COLUMN_TYPE_ID, typeId);
        values.put(ActivityEntry.COLUMN_TITLE, title);
        values.put(ActivityEntry.COLUMN_IMAGE_RESOURCE, imageResource);

        return database.insert(ActivityEntry.TABLE_NAME, null, values);
    }

    public long createActivityLog(long activityId, int value, String date) {
        ContentValues values = new ContentValues();
        values.put(ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID, activityId);
        values.put(ActivityLogEntry.COLUMN_VALUE, value);
        values.put(ActivityLogEntry.COLUMN_LOG_DATE, date);
        return database.insert(ActivityLogEntry.TABLE_NAME, null, values);
    }

    //READ
    public ArrayList<Activity> getAllActivities() {
        ArrayList<Activity> activities = new ArrayList<>();
        String query = "Select " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID  +
                ", title, image_resource, " +  TypeEntry.TABLE_NAME + "." +  TypeEntry._ID + " FROM " +
                ActivityEntry.TABLE_NAME + " INNER JOIN " + TypeEntry.TABLE_NAME + " ON " +
                ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." +TypeEntry._ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Activity activity = new Activity();
                activity.setId(cursor.getInt(0));
                activity.setTitle(cursor.getString(1));
                activity.setImageResourceId(cursor.getInt(2));
                activity.setTypeId(cursor.getInt(3));
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  activities;
    }

    public ArrayList<SubActivity> getSubActivities(long activityId) {
        ArrayList<SubActivity> subActivities = new ArrayList<>();
        /**
         * Ambil data
         *
         * Sub Activity
         * Title
         * Target Value
         * Is Completed
         *
         * Type
         * Unit Name
         */
        String query = "Select " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID  + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_TITLE  + ", " +
                SubActivityEntry.COLUMN_TARGET_VALUE + ", " +
                SubActivityEntry.COLUMN_IS_COMPLETED + ", " +
                TypeEntry.TABLE_NAME + "." +  TypeEntry.COLUMN_UNIT_NAME + ", " +
                "IFNULL(SUM(" + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_VALUE + "), 0) AS current_value " +
                " FROM " + SubActivityEntry.TABLE_NAME + " " +
                "INNER JOIN " + ActivityEntry.TABLE_NAME + " ON " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID +
                " INNER JOIN " + TypeEntry.TABLE_NAME + " ON " + ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID +
                " LEFT JOIN " + ActivityLogEntry.TABLE_NAME + " ON " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID +
                " WHERE " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + activityId + " " +
                "GROUP BY " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SubActivity subActivity = new SubActivity();
                subActivity.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry._ID)));
                subActivity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TITLE)));
                subActivity.setTargetValue(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TARGET_VALUE)));
                subActivity.setIsCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_IS_COMPLETED)));
                subActivity.setUnitName(cursor.getString(cursor.getColumnIndexOrThrow(TypeEntry.COLUMN_UNIT_NAME)));
                subActivity.setCurrentValue(cursor.getInt(cursor.getColumnIndexOrThrow("current_value")));
                subActivities.add(subActivity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subActivities;
    }


    public ArrayList<Type> getAllTypes() {
        ArrayList<Type> types = new ArrayList<>();
        String query = "SELECT _id, category, unit_name FROM " + TypeEntry.TABLE_NAME;
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
        String query = "Select _id, type FROM " + ScheduleEntry.TABLE_NAME;
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

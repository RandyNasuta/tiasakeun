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

    public long createSubActivity(long activityId, long schduleId, String title, int targetValuye, String dateActivityt, int notification, int isCompleted) {
        ContentValues values = new ContentValues();
        values.put(SubActivityEntry.COLUMN_ACTIVITY_ID, activityId);
        values.put(SubActivityEntry.COLUMN_SCHEDULE_ID, schduleId);
        values.put(SubActivityEntry.COLUMN_TITLE, title);
        values.put(SubActivityEntry.COLUMN_TARGET_VALUE, targetValuye);
        values.put(SubActivityEntry.COLUMN_DATE_ACTIVITY, dateActivityt);
        values.put(SubActivityEntry.COLUMN_NOTIFICATION, notification);
        values.put(SubActivityEntry.COLUMN_IS_COMPLETED, isCompleted);
        return database.insert(SubActivityEntry.TABLE_NAME, null, values);
    }

    public long createLogActivity(long subActivityId, int value, String date) {
        ContentValues values = new ContentValues();
        values.put(ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID, subActivityId);
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

    public ArrayList<SubActivity> getSubActivitiesByActivityId(long activityId) {
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
                SubActivityEntry.COLUMN_NOTIFICATION + ", " +
                SubActivityEntry.COLUMN_DATE_ACTIVITY + ", " +
                SubActivityEntry.COLUMN_TARGET_VALUE + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_ACTIVITY_ID + ", " +
                ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_TYPE + ", " +
                TypeEntry.TABLE_NAME + "." +  TypeEntry.COLUMN_UNIT_NAME + ", " +
                "IFNULL(SUM(" + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_VALUE + "), 0) AS current_value " +
                " FROM " + SubActivityEntry.TABLE_NAME +
                " INNER JOIN " + ActivityEntry.TABLE_NAME + " ON " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID +
                " INNER JOIN " + TypeEntry.TABLE_NAME + " ON " + ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID +
                " INNER JOIN " + ScheduleEntry.TABLE_NAME + " ON " + SubActivityEntry.COLUMN_SCHEDULE_ID + " = " + ScheduleEntry.TABLE_NAME + "." + TypeEntry._ID +
                " LEFT JOIN " + ActivityLogEntry.TABLE_NAME + " ON " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID +
                " WHERE " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + activityId +
                " GROUP BY " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SubActivity subActivity = new SubActivity();
                subActivity.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry._ID)));
                subActivity.setActivityId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_ACTIVITY_ID)));
                subActivity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TITLE)));
                subActivity.setDateActivity(cursor.getString(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_DATE_ACTIVITY)));
                subActivity.setTargetValue(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TARGET_VALUE)));
                subActivity.setNotification(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_NOTIFICATION)));
                subActivity.setScheduleType(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_TYPE)));
                subActivity.setTargetValue(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TARGET_VALUE)));
                subActivity.setUnitName(cursor.getString(cursor.getColumnIndexOrThrow(TypeEntry.COLUMN_UNIT_NAME)));
                subActivity.setCurrentValue(cursor.getInt(cursor.getColumnIndexOrThrow("current_value")));
                subActivities.add(subActivity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subActivities;
    }

    public SubActivity getSubActivityById(long id) {
        SubActivity subActivity = new SubActivity();
        String query = "Select * FROM " + SubActivityEntry.TABLE_NAME + " WHERE " + SubActivityEntry._ID + " = " + id;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                subActivity.setId(cursor.getLong(0));
                subActivity.setActivityId(cursor.getInt(1));
                subActivity.setScheduleId(cursor.getLong(2));
                subActivity.setTitle(cursor.getString(3));
                subActivity.setTargetValue(cursor.getInt(4));
                subActivity.setDateActivity(cursor.getString(5));
                subActivity.setNotification(cursor.getInt(6));
                subActivity.setIsCompleted(cursor.getInt(7));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return subActivity;
    }

    public String getCategoryTypeById(long activityId) {
        String categoryType = "";

        String query = "Select category FROM " + TypeEntry.TABLE_NAME +
                " INNER JOIN " + ActivityEntry.TABLE_NAME +
                " ON " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID + " = " + ActivityEntry.TABLE_NAME + "." + ActivityEntry.COLUMN_TYPE_ID + " " +
                "WHERE " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID + " = " + activityId;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                categoryType = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return categoryType;
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

    public int getValueProgress(long id) {
        int progress = 0;
        String query = "Select " + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_VALUE + " FROM " + ActivityLogEntry.TABLE_NAME +
                " INNER JOIN " + SubActivityEntry.TABLE_NAME + " ON " + ActivityLogEntry.TABLE_NAME + "." + ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " WHERE " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + id;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                progress = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return progress;
    }

    public Schedule getScheduleByType(String type) {
        Schedule schedule = null;
        String query = "Select _id, type FROM " + ScheduleEntry.TABLE_NAME + " WHERE type LIKE '" + type + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            schedule = new Schedule(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();
        return schedule;
    }

    public String getTypeCategory(long activityId) {
        String category = "";
        String query = "SELECT " + TypeEntry.TABLE_NAME + "." + TypeEntry.COLUMN_CATEGORY + " FROM " + TypeEntry.TABLE_NAME + " " +
                        "INNER JOIN " + ActivityEntry.TABLE_NAME + " ON " + ActivityEntry.TABLE_NAME + "." + ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID + " " +
                        "WHERE " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID + " = " + activityId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                category = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        return category;
    }

    //UPDATE
    public long updateSubActivity(long id, long ScheduleId, String title, int targetValue, String dateActivity, int notification) {
        ContentValues values = new ContentValues();
        values.put(SubActivityEntry.COLUMN_SCHEDULE_ID, ScheduleId);
        values.put(SubActivityEntry.COLUMN_TITLE, title);
        values.put(SubActivityEntry.COLUMN_TARGET_VALUE, targetValue);
        values.put(SubActivityEntry.COLUMN_DATE_ACTIVITY, dateActivity);
        values.put(SubActivityEntry.COLUMN_NOTIFICATION, notification);

        String whereClause = SubActivityEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(SubActivityEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    public long updateCompletedSubActivity(long id) {
        ContentValues values = new ContentValues();
        values.put(SubActivityEntry.COLUMN_IS_COMPLETED, 1);
        String whereClause = SubActivityEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(SubActivityEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    //DELETE
    public boolean deleteActivityLogs(long subActivityId) {
        String whereClause = ActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = ?";
        String[] whereArgs = {String.valueOf(subActivityId)};
        int rowsAffected = database.delete(ActivityLogEntry.TABLE_NAME, whereClause, whereArgs);
        return rowsAffected > 0;
    }
}

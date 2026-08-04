package com.example.tiasakeun.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tiasakeun.data.local.ActivityContract.ActivityEntry;
import com.example.tiasakeun.data.local.SubActivityContract.SubActivityEntry;
import com.example.tiasakeun.data.local.SubActivityLogContract.SubActivityLogEntry;
import com.example.tiasakeun.data.local.DbHelper;
import com.example.tiasakeun.data.local.ScheduleContract.ScheduleEntry;
import com.example.tiasakeun.data.local.TypeContract.TypeEntry;
import com.example.tiasakeun.data.model.Activity;
import com.example.tiasakeun.data.model.Schedule;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.model.SubActivityLog;
import com.example.tiasakeun.data.model.Type;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DatabaseDataSource {
    private final String TAG = "DatabaseDataSource";
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

    public long createSubActivity(long activityId, long scheduleId, String title, long targetValue, String dateActivity, int notification, int isCompleted) {
        ContentValues values = new ContentValues();
        values.put(SubActivityEntry.COLUMN_ACTIVITY_ID, activityId);
        values.put(SubActivityEntry.COLUMN_SCHEDULE_ID, scheduleId);
        values.put(SubActivityEntry.COLUMN_TITLE, title);
        values.put(SubActivityEntry.COLUMN_TARGET_VALUE, targetValue);
        values.put(SubActivityEntry.COLUMN_DATE_ACTIVITY, dateActivity);
        values.put(SubActivityEntry.COLUMN_NOTIFICATION, notification);
        values.put(SubActivityEntry.COLUMN_IS_COMPLETED, isCompleted);
        return database.insert(SubActivityEntry.TABLE_NAME, null, values);
    }

    public long createLogActivity(long subActivityId, long value, String date) {
        ContentValues values = new ContentValues();
        values.put(SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID, subActivityId);
        values.put(SubActivityLogEntry.COLUMN_VALUE, value);
        values.put(SubActivityLogEntry.COLUMN_LOG_DATE, date);
        return database.insert(SubActivityLogEntry.TABLE_NAME, null, values);
    }

    //READ
    public SubActivity getLastSubActivityByActivityId(Long activityId) {
        SubActivity subActivity = new SubActivity();
        String query = "SELECT * FROM sub_activities WHERE activity_id = " + activityId + " ORDER BY id DESC LIMIT 1";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                subActivity.setId(cursor.getLong(0));
                subActivity.setActivityId(cursor.getLong(1));
                subActivity.setScheduleId(cursor.getLong(2));
                subActivity.setTitle(cursor.getString(3));
                subActivity.setTargetValue(cursor.getLong(4));
                subActivity.setDateActivity(cursor.getString(5));
                subActivity.setNotification(cursor.getInt(6));
                subActivity.setIsCompleted(cursor.getInt(7));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return subActivity;
    }

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
                activity.setId(cursor.getLong(0));
                activity.setTitle(cursor.getString(1));
                activity.setImageResourceId(cursor.getInt(2));
                activity.setTypeId(cursor.getLong(3));
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  activities;
    }

    public ArrayList<SubActivity> getSubActivitiesToday(Long activityId, Integer isCompleted, String categoryType) {
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
        String todayFilter = "date(" + SubActivityLogEntry.TABLE_NAME  + "." + SubActivityLogEntry.COLUMN_LOG_DATE + ") > date('now', 'localtime')";
        String currentValueQuery;

        if (categoryType.equals("Waktu")) {
            currentValueQuery = "IFNULL(" + "SUM(" + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_VALUE + ")" + ", 0)";
        } else {
            currentValueQuery = "IFNULL((SELECT " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_VALUE + " " +
                                "FROM " + SubActivityLogEntry.TABLE_NAME + " " +
                                "WHERE " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " " +
                                " AND " + SubActivityLogEntry.COLUMN_LOG_DATE + " >= date('now', 'localtime') " +
                                "ORDER BY " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_LOG_DATE + " DESC LIMIT 1), 0)";
        }

        String query = "Select " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID  + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_TITLE  + ", " +
                SubActivityEntry.COLUMN_TARGET_VALUE + ", " +
                SubActivityEntry.COLUMN_NOTIFICATION + ", " +
                SubActivityEntry.COLUMN_DATE_ACTIVITY + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_ACTIVITY_ID + ", " +
                ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_TYPE + ", " +
                TypeEntry.TABLE_NAME + "." +  TypeEntry.COLUMN_UNIT_NAME + ", " +
                currentValueQuery + " AS current_value " + " " +
                "FROM " + SubActivityEntry.TABLE_NAME + " " +
                "INNER JOIN " + ActivityEntry.TABLE_NAME + " ON " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID + " " +
                "INNER JOIN " + TypeEntry.TABLE_NAME + " ON " + ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID + " " +
                "INNER JOIN " + ScheduleEntry.TABLE_NAME + " ON " + SubActivityEntry.COLUMN_SCHEDULE_ID + " = " + ScheduleEntry.TABLE_NAME + "." + ScheduleEntry._ID + " " +
                "LEFT JOIN " + SubActivityLogEntry.TABLE_NAME + " ON " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " AND " + todayFilter + " " +
                "WHERE " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + activityId +
                (isCompleted != null ? " AND " + SubActivityEntry.COLUMN_IS_COMPLETED + " = " + isCompleted : "") + " " +
                "GROUP BY " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID;

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
                subActivity.setActivityId(cursor.getLong(1));
                subActivity.setScheduleId(cursor.getLong(2));
                subActivity.setTitle(cursor.getString(3));
                subActivity.setTargetValue(cursor.getLong(4));
                subActivity.setDateActivity(cursor.getString(5));
                subActivity.setNotification(cursor.getInt(6));
                subActivity.setIsCompleted(cursor.getInt(7));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return subActivity;
    }

    public ArrayList<SubActivity> getSubActivitiesByActivityId(long activityId) {
        ArrayList<SubActivity> subActivities = new ArrayList<>();
        String query = "Select * FROM " + SubActivityEntry.TABLE_NAME + " WHERE " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + activityId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                subActivities.add(new SubActivity(
                        (cursor.getLong(0)),
                        (cursor.getLong(1)),
                        (cursor.getLong(2)),
                        (cursor.getString(3)),
                        (cursor.getLong(4)),
                        (cursor.getString(5)),
                        (cursor.getInt(6)),
                        (cursor.getInt(7))
                ));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return subActivities;
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

    public int getValueProgress(long id, String categoryType) {
        int progress = 0;
        String query;

        String todayFilter = "date(" + SubActivityLogEntry.TABLE_NAME  + "." + SubActivityLogEntry.COLUMN_LOG_DATE + ") = date('now', 'localtime')";
        if (categoryType.equals("Waktu")) {
            query = "Select SUM(" + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_VALUE + ") FROM " + SubActivityLogEntry.TABLE_NAME + " " +
                    "INNER JOIN " + SubActivityEntry.TABLE_NAME + " ON " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " +
                    SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " WHERE " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + id + " AND " +
                    todayFilter;
        } else {
            query = "Select " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_VALUE + " FROM " + SubActivityLogEntry.TABLE_NAME +
                    " INNER JOIN " + SubActivityEntry.TABLE_NAME + " ON " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " +
                    SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " WHERE " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " = " + id + " AND " +
                    todayFilter + " " +
                    "ORDER BY " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_LOG_DATE + " DESC " +
                    "LIMIT 1";
        }

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                progress = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

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
        cursor.close();

        return category;
    }

    public ArrayList<SubActivityLog> getSubActivityLogs(Long subActivityId, String duration, String sorting) {
        Log.i(TAG, "getSubActivityLogs: Start | subActivityId = " + subActivityId + " | duration = " + duration + " | sorting = " + sorting);
        ArrayList<SubActivityLog> subActivityLogs = new ArrayList<>();

        String query = "SELECT " +
                SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry._ID + ", " +
                SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_VALUE + ", " +
                SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_LOG_DATE + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + ", " +
                SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_TITLE + ", " +
                TypeEntry.TABLE_NAME + "." + TypeEntry.COLUMN_CATEGORY + ", " +
                TypeEntry.TABLE_NAME + "." + TypeEntry.COLUMN_UNIT_NAME +
                " FROM " + SubActivityLogEntry.TABLE_NAME + " " +
                "INNER JOIN " + SubActivityEntry.TABLE_NAME + " ON " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry._ID + " " +
                "INNER JOIN " + ActivityEntry.TABLE_NAME + " ON " + SubActivityEntry.TABLE_NAME + "." + SubActivityEntry.COLUMN_ACTIVITY_ID + " = " + ActivityEntry.TABLE_NAME + "." + ActivityEntry._ID + " " +
                "INNER JOIN " + TypeEntry.TABLE_NAME + " ON " + ActivityEntry.TABLE_NAME + "." + ActivityEntry.COLUMN_TYPE_ID + " = " + TypeEntry.TABLE_NAME + "." + TypeEntry._ID + " " +
                "WHERE " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " + subActivityId + " " +
                (!duration.equals("") ? "AND " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_LOG_DATE + " >= date('now', 'localtime', '" + duration + "') " : "") +
                "ORDER BY " + SubActivityLogEntry.TABLE_NAME + "." + sorting;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                subActivityLogs.add(new SubActivityLog(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.i(TAG, "getSubActivityLogs: data | total = " + subActivityLogs.size());
        for (SubActivityLog s : subActivityLogs) {
            Log.i(TAG, s.toString());
        }
        return subActivityLogs;
    }

    public LinkedHashMap<String, Long> getDailyChartSummary(Long subActivityId, String duration) {
        LinkedHashMap<String, Long> summary = new LinkedHashMap<>();

        String dateFilter = "";
        if (!duration.isEmpty()) {
            dateFilter = " AND " + SubActivityLogEntry.TABLE_NAME + "." + SubActivityLogEntry.COLUMN_LOG_DATE + " >= date('now', 'localtime', '" + duration + "')";
        }

        String query = "SELECT date(" + SubActivityLogEntry.COLUMN_LOG_DATE + ") as log_day, " +
                "SUM(" + SubActivityLogEntry.COLUMN_VALUE + ") as total_value " +
                "FROM " + SubActivityLogEntry.TABLE_NAME + " " +
                "WHERE " + SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = " + subActivityId + " " +
                dateFilter +
                " GROUP BY log_day " +
                " ORDER BY log_day ASC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do{
                String date = cursor.getString(0);
                long totalValue = cursor.getLong(1);
                summary.put(date, totalValue);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return summary;
    }

    public ArrayList<SubActivity> getIncompleteSubActivitiesByDate(String date) {
        ArrayList<SubActivity> incompleteSubActivities = new ArrayList<>();

        Cursor cursor = database.query(
                SubActivityEntry.TABLE_NAME,
                null,
                SubActivityEntry.COLUMN_DATE_ACTIVITY + " LIKE ? AND " + SubActivityEntry.COLUMN_IS_COMPLETED + " = ?",
                new String[]{date + "%", "0"},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                SubActivity subActivity = new SubActivity();
                subActivity.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry._ID)));
                subActivity.setActivityId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_ACTIVITY_ID)));
                subActivity.setScheduleId(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_SCHEDULE_ID)));
                subActivity.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TITLE)));
                subActivity.setTargetValue(cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_TARGET_VALUE)));
                subActivity.setDateActivity(cursor.getString(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_DATE_ACTIVITY)));
                subActivity.setNotification(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_NOTIFICATION)));
                subActivity.setIsCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(SubActivityEntry.COLUMN_IS_COMPLETED)));
                incompleteSubActivities.add(subActivity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return incompleteSubActivities;
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

    public boolean updateActivityTitle(long id, String title) {
        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(ActivityEntry.COLUMN_TITLE, title);

            String whereClause = ActivityEntry._ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            int rowsAffected =  database.update(ActivityEntry.TABLE_NAME, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                database.setTransactionSuccessful();
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "updateActivityTitle: " + e.getMessage());
            return false;
        } finally {
            database.endTransaction();
        }
    }

    //DELETE
    public boolean deleteActivity(long activityId) {
        database.beginTransaction();

        try {
            String query = "SELECT " + SubActivityEntry._ID + " FROM " + SubActivityEntry.TABLE_NAME + " " +
                    "WHERE " + SubActivityEntry.COLUMN_ACTIVITY_ID + " = ? ";

            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(activityId)});

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long subActivityId = cursor.getLong(cursor.getColumnIndexOrThrow(SubActivityEntry._ID));

                    database.delete(
                            SubActivityLogEntry.TABLE_NAME,
                            SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = ?",
                            new String[]{String.valueOf(subActivityId)}
                    );
                }

                cursor.close();
            }

            database.delete(
                    SubActivityEntry.TABLE_NAME,
                    SubActivityEntry.COLUMN_ACTIVITY_ID + " = ?",
                    new String[]{String.valueOf(activityId)}
            );

            int rowAffected = database.delete(
                    ActivityEntry.TABLE_NAME,
                    ActivityEntry._ID + " = ?",
                    new String[]{String.valueOf(activityId)}
            );

            if (rowAffected > 0) {
                database.setTransactionSuccessful();
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "deleteActivity: " + e.getMessage());
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public boolean deleteActivityLogs(long subActivityId) {
        String whereClause = SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = ? AND date(" + SubActivityLogEntry.COLUMN_LOG_DATE + ") = date('now', 'localtime')";
        String[] whereArgs = {String.valueOf(subActivityId)};
        int rowsAffected = database.delete(SubActivityLogEntry.TABLE_NAME, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean deleteSubActivity(long subActivityId) {
        database.beginTransaction();

        try {
            database.delete(
                    SubActivityLogEntry.TABLE_NAME,
                    SubActivityLogEntry.COLUMN_SUB_ACTIVITY_ID + " = ?",
                    new String[]{String.valueOf(subActivityId)}
            );


            int rowsAffected = database.delete(
                    SubActivityEntry.TABLE_NAME,
                    SubActivityEntry._ID + " = ?",
                    new String[]{String.valueOf(subActivityId)}
            );

            if (rowsAffected> 0) {
                database.setTransactionSuccessful();
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "deleteSubActivity: " + e.getMessage());
            return false;
        } finally {
            database.endTransaction();
        }
    }
}

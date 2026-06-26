package com.example.tiasakeun.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tiasakeun.data.local.UserContract.UserEntry;
import com.example.tiasakeun.data.local.TypeContract.TypeEntry;
import com.example.tiasakeun.data.local.ActivityContract.ActivityEntry;
import com.example.tiasakeun.data.local.ScheduleContract.ScheduleEntry;
import com.example.tiasakeun.data.local.ActivityLogContract.ActivityLogEntry;
import com.example.tiasakeun.data.local.NotificationContract.NotificationEntry;

/**
 * Class untuk membuat database, relasi table, dan migrasi database
 */
public class DbHelper extends SQLiteOpenHelper {
    // Variabel versi dan nama database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tiasakeun.db";

    //Query DDL SQL dari class contract
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    UserEntry.COLUMN_PIN + " TEXT NOT NULL, " +
                    UserEntry.COLUMN_SECURITY_QUESTION + " TEXT NOT NULL, " +
                    UserEntry.COLUMN_SECURITY_ANSWER + " TEXT NOT NULL)";

    private static final String SQL_CREATE_TYPES =
            "CREATE TABLE " + TypeEntry.TABLE_NAME + " (" +
                TypeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TypeEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                TypeEntry.COLUMN_UNIT_NAME + " TEXT NOT NULL)";

    private static final String SQL_CREATE_SCHEDULES =
            "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                    ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ScheduleEntry.COLUMN_TYPE + " TEXT NOT NULL)";

    private static final String SQL_CREATE_ACTIVITIES =
            "CREATE TABLE " + ActivityEntry.TABLE_NAME + " (" +
                    ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ActivityEntry.COLUMN_USER_ID + " INTEGER, " +
                    ActivityEntry.COLUMN_TYPE_ID + " INTEGER, " +
                    ActivityEntry.COLUMN_SCHEDULE_ID + " INTEGER, " +
                    ActivityEntry.COLUMN_DATE_ACTIVITY + " TEXT NOT NULL, " +
                    ActivityEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    ActivityEntry.COLUMN_IS_COMPLETED + " INTEGER NOT NULL, " +
                    ActivityEntry.COLUMN_CURRENT_VALUE + " INTEGER NOT NULL, " +
                    ActivityEntry.COLUMN_TARGET_VALUE + " INTEGER NOT NULL, " +
                    ActivityEntry.COLUMN_NOTIFICATION + " INTEGER NOT NULL, " +
                    ActivityEntry.COLUMN_IMAGE_RESOURCE + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + ActivityEntry.COLUMN_USER_ID + ") REFERENCES " +
                    UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), " +
                    "FOREIGN KEY (" + ActivityEntry.COLUMN_TYPE_ID + ") REFERENCES " +
                    TypeEntry.TABLE_NAME + "(" + TypeEntry._ID + "), " +
                    "FOREIGN KEY (" + ActivityEntry.COLUMN_SCHEDULE_ID + ") REFERENCES " +
                    ScheduleEntry.TABLE_NAME + "(" + ScheduleEntry._ID + ") " +
                    "ON DELETE CASCADE)";

    private static final String SQL_CREATE_ACTIVITY_LOGS =
            "CREATE TABLE " + ActivityLogEntry.TABLE_NAME + " (" +
                    ActivityLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ActivityLogEntry.COLUMN_ACTIVITY_ID + " INTEGER, " +
                    ActivityLogEntry.COLUMN_VALUE + " INTEGER NOT NULL, " +
                    ActivityLogEntry.COLUMN_DATE  + " TEXT DEFAULT (datetime('now', 'localtime')), " +
                    "FOREIGN KEY (" + ActivityLogEntry.COLUMN_ACTIVITY_ID + ") REFERENCES " +
                    ActivityEntry.TABLE_NAME + "(" + ActivityEntry._ID + ") " +
                    "ON DELETE CASCADE)";

    private static final String SQL_CREATE_NOTIFICATIONS =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NotificationEntry.COLUMN_ACTIVITY_ID + " INTEGER, " +
                    NotificationEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + NotificationEntry.COLUMN_ACTIVITY_ID + ") REFERENCES " +
                    ActivityEntry.TABLE_NAME + "(" + ActivityEntry._ID + ") " +
                    "ON DELETE CASCADE)";

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
    private static final String SQL_DELETE_TYPES =
            "DROP TABLE IF EXISTS " + TypeEntry.TABLE_NAME;

    private static final String SQL_DELETE_SCHEDULES =
            "DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME;

    private static final String SQL_DELETE_ACTIVITIES =
            "DROP TABLE IF EXISTS " + ActivityEntry.TABLE_NAME;

    private static final String SQL_DELETE_ACTIVITY_LOGS =
            "DROP TABLE IF EXISTS " + ActivityLogEntry.TABLE_NAME;

    private static final String SQL_DELETE_NOTIFICATIONS =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TYPES);
        sqLiteDatabase.execSQL(SQL_CREATE_SCHEDULES);
        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVITIES);
        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVITY_LOGS);
        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATIONS);

        //Data master
        sqLiteDatabase.execSQL("INSERT INTO " + UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_NAME + ", " + UserEntry.COLUMN_PIN + ", " + UserEntry.COLUMN_SECURITY_QUESTION + ", " + UserEntry.COLUMN_SECURITY_ANSWER + ") VALUES ('User', '123456', 'Siapa nama kucing peliharaanmu?', 'Kucing')");

        sqLiteDatabase.execSQL("INSERT INTO " + TypeEntry.TABLE_NAME + " (" + TypeEntry.COLUMN_CATEGORY + ", " + TypeEntry.COLUMN_UNIT_NAME + ") VALUES ('Waktu', 'menit')");
        sqLiteDatabase.execSQL("INSERT INTO " + TypeEntry.TABLE_NAME + " (" + TypeEntry.COLUMN_CATEGORY + ", " + TypeEntry.COLUMN_UNIT_NAME + ") VALUES ('Jumlah', 'mililiter')");
        sqLiteDatabase.execSQL("INSERT INTO " + TypeEntry.TABLE_NAME + " (" + TypeEntry.COLUMN_CATEGORY + ", " + TypeEntry.COLUMN_UNIT_NAME + ") VALUES ('Jumlah', 'repetisi')");
        sqLiteDatabase.execSQL("INSERT INTO " + TypeEntry.TABLE_NAME + " (" + TypeEntry.COLUMN_CATEGORY + ", " + TypeEntry.COLUMN_UNIT_NAME + ") VALUES ('Jumlah', 'meter')");

        sqLiteDatabase.execSQL("INSERT INTO " + ScheduleEntry.TABLE_NAME + " (" + ScheduleEntry.COLUMN_TYPE + ") VALUES ('Harian')");
        sqLiteDatabase.execSQL("INSERT INTO " + ScheduleEntry.TABLE_NAME + " (" + ScheduleEntry.COLUMN_TYPE + ") VALUES ('Mingguan')");
        sqLiteDatabase.execSQL("INSERT INTO " + ScheduleEntry.TABLE_NAME + " (" + ScheduleEntry.COLUMN_TYPE + ") VALUES ('Bulanan')");
        sqLiteDatabase.execSQL("INSERT INTO " + ScheduleEntry.TABLE_NAME + " (" + ScheduleEntry.COLUMN_TYPE + ") VALUES ('Tahunan')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_NOTIFICATIONS);
        sqLiteDatabase.execSQL(SQL_DELETE_ACTIVITY_LOGS);
        sqLiteDatabase.execSQL(SQL_DELETE_ACTIVITIES);
        sqLiteDatabase.execSQL(SQL_DELETE_SCHEDULES);
        sqLiteDatabase.execSQL(SQL_DELETE_TYPES);
        sqLiteDatabase.execSQL(SQL_DELETE_USERS);

        onCreate(sqLiteDatabase);
    }
}

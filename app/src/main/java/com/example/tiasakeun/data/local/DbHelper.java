package com.example.tiasakeun.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tiasakeun.data.local.UserContract.UserEntry;
import com.example.tiasakeun.data.local.TypeContract.TypeEntry;
import com.example.tiasakeun.data.local.ScheduleContract.ScheduleEntry;

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

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
    private static final String SQL_DELETE_TYPES =
            "DROP TABLE IF EXISTS " + TypeEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USERS);
        sqLiteDatabase.execSQL(SQL_CREATE_TYPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_USERS);
        sqLiteDatabase.execSQL(SQL_DELETE_TYPES);
        onCreate(sqLiteDatabase);
    }
}

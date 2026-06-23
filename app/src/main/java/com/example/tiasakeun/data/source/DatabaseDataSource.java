package com.example.tiasakeun.data.source;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tiasakeun.data.local.DbHelper;
import com.example.tiasakeun.data.local.ScheduleContract;
import com.example.tiasakeun.data.local.TypeContract;
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

    //READ
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

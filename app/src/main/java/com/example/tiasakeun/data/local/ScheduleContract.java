package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class ScheduleContract {
    private ScheduleContract() {};

    public static class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedules";
        public static final String COLUMN_TYPE = "type";
    }
}

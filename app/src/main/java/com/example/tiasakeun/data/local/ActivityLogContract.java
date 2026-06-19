package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class ActivityLogContract {
    private ActivityLogContract() {};

    public static class ActivityLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "activity_logs";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_DATE = "date";
    }
}

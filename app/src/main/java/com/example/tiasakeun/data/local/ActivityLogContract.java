package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class ActivityLogContract {
    private ActivityLogContract() {};

    public static class ActivityLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "activity_logs";
        public static final String COLUMN_SUB_ACTIVITY_ID = "sub_activity_id";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_LOG_DATE = "log_date";
    }
}

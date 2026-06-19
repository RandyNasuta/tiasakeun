package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class NotificationContract {
    private NotificationContract() {}

    public static class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_TIME = "time";
    }
}

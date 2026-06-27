package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class SubActivityContract {

    private SubActivityContract() {}

    public static class SubActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "sub_activities";
        public static final String COLUMN_ACTIVITY_ID = "activity_id";
        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TARGET_VALUE = "target_value";
        public static final String COLUMN_DATE_ACTIVITY = "date_activity";
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_IS_COMPLETED = "is_completed";
    }
}

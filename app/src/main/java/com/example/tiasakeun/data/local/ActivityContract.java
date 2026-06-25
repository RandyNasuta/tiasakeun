package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public class ActivityContract {

    private ActivityContract() {}

    public static class ActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "activities";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_DATE_ACTIVITY = "date_activity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CURRENT_VALUE = "current_value";
        public static final String COLUMN_TARGET_VALUE = "target_value";
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
    }
}

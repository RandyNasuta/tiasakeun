package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public final class TypeContract {

    private TypeContract() {};

    public static class TypeEntry implements BaseColumns {
        public static String TABLE_NAME = "types";
        public static String COLUMN_CATEGORY = "category";
        public static String COLUMN_UNIT_NAME = "unit_name";
    }
}

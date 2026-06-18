package com.example.tiasakeun.data.local;

import android.provider.BaseColumns;

public final class UserContract {

    //Mencegah dari instansiasi kelas secara tidak sengaja
    private UserContract() {};

    //Inner class yang didefiniskan sebagai isi table
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PIN = "pin";
        public static final String COLUMN_SECURITY_QUESTION = "security_question";
        public static final String COLUMN_SECURITY_ANSWER = "security_answer";
    }


}

package com.simonebakker.simone.addemup.database;

import android.provider.BaseColumns;

public final class GameContract {

    private GameContract() {}

    /* Inner class that defines the table contents */
    public static class GameEntry implements BaseColumns {
        public static final String TABLE_NAME = "Game";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_POINTS = "points";
        public static final String COLUMN_NAME_PROGRESS = "progress";
        public static final String COLUMN_NAME_DATE = "date";
    }
}

package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private MountainReaderContract(){}

    // Inner class that defines the Mountain table contents
    public static class MountainEntry implements BaseColumns {
        public static final String TABLE_NAME = "mountain";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_LOCATION = "location";
    }

    public static final String SQL_STRING = "CREATE TABLE IF NOT EXISTS " +
                    MountainEntry.TABLE_NAME + " (" +
                    MountainEntry._ID + " INTEGER PRIMARY KEY," +
                    MountainEntry.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE," +
                    MountainEntry.COLUMN_NAME_HEIGHT + " INTEGER,"+
                    MountainEntry.COLUMN_NAME_LOCATION + " TEXT" + ")";
}

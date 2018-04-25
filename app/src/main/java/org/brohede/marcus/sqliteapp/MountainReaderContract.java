package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

import static org.brohede.marcus.sqliteapp.MountainReaderContract.MountainEntry.TABLE_NAME;

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

    public static final String SQL_STRING = "CREATE DATABASE " + MountainEntry.TABLE_NAME + " (" +
                    MountainEntry._ID + " INTEGER PRIMARY KEY," +
                    MountainEntry.COLUMN_NAME_NAME + " TEXT," +
                    MountainEntry.COLUMN_NAME_HEIGHT + " TEXT,"+
                    MountainEntry.COLUMN_NAME_LOCATION + " TEXT" + ")";


}

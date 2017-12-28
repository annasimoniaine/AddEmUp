package com.simonebakker.simone.addemup.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // Version number to upgrade database version
    // each time if you Add, Edit table, you need to change the
    // version number.
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 2;
    private final Context context;
    // Creating the table
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + GameContract.GameEntry.TABLE_NAME +
                    "(" +
                    GameContract.GameEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + GameContract.GameEntry.COLUMN_NAME_NAME + " TEXT, "
                    + GameContract.GameEntry.COLUMN_NAME_POINTS + " INTEGER, "
                    + GameContract.GameEntry.COLUMN_NAME_PROGRESS + " INTEGER, "
                    + GameContract.GameEntry.COLUMN_NAME_DATE + " TEXT ) ";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(DATABASE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + GameContract.GameEntry.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }
}

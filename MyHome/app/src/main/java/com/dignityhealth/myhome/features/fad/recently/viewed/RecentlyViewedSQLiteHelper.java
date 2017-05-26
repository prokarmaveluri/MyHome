package com.dignityhealth.myhome.features.fad.recently.viewed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Chandra on 23-08-2016.
 */
public class RecentlyViewedSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "table_recently_viewed";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROVIDER_ID = "_provider_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String DATABASE_NAME = "recently.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_PROVIDER_ID + " varchar not null,  " + COLUMN_TIMESTAMP
            + " DATETIME DEFAULT CURRENT_TIMESTAMP" + " )";

    public RecentlyViewedSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RecentlyViewedSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
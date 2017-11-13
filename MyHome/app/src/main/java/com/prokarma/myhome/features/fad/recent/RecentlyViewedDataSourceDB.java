package com.prokarma.myhome.features.fad.recent;

/**
 * Created by Chandra on 23-08-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;

import java.util.ArrayList;

/**
 * Created by Chandra on 23-08-2016.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class RecentlyViewedDataSourceDB {

    private SQLiteDatabase database;
    private RecentlyViewedSQLiteHelper dbHelper;
    private static final int MAX_ROW_COUNT = 10;

    private String[] allColumns = {RecentlyViewedSQLiteHelper.COLUMN_ID,
            RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID, RecentlyViewedSQLiteHelper.COLUMN_PROVIDER};

    private String[] columnsToUpdate = {
            RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID,
            RecentlyViewedSQLiteHelper.COLUMN_PROVIDER,
            RecentlyViewedSQLiteHelper.COLUMN_TIMESTAMP};

    private static RecentlyViewedDataSourceDB ourInstance = null;

    public static RecentlyViewedDataSourceDB getInstance() {
        if (null == ourInstance) {
            ourInstance = new RecentlyViewedDataSourceDB();
        }
        return ourInstance;
    }

    private RecentlyViewedDataSourceDB() {
    }

    public void open(Context context) throws SQLException {
        dbHelper = new RecentlyViewedSQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<String> getAllEntry() {
        ArrayList<String> entryList = new ArrayList<>();

        Cursor cursor = database.query(RecentlyViewedSQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, RecentlyViewedSQLiteHelper.COLUMN_TIMESTAMP + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entryList.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        return entryList;
    }

    public ArrayList<String> getAllProviderEntry() {
        ArrayList<String> entryList = new ArrayList<>();

        Cursor cursor = database.query(RecentlyViewedSQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, RecentlyViewedSQLiteHelper.COLUMN_TIMESTAMP + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entryList.add(cursor.getString(2));
            cursor.moveToNext();
        }
        cursor.close();
        return entryList;
    }

    public void createEntry(ProviderDetailsResponse provider) {
        String providerId = provider.getNpi();
        Gson gson = new Gson();
        String jsonString = gson.toJson(provider);
        String dbPin = getMyEntry(providerId);
        if (dbPin != null) {
            deleteEntry(providerId);
        }

        ContentValues values = new ContentValues();
        values.put(RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID, providerId);
        values.put(RecentlyViewedSQLiteHelper.COLUMN_PROVIDER, jsonString);
        if (getRowCount() >= MAX_ROW_COUNT) {
            String query = "DELETE FROM " + RecentlyViewedSQLiteHelper.TABLE_NAME + " WHERE " +
                    RecentlyViewedSQLiteHelper.COLUMN_ID + " IN  (SELECT " + RecentlyViewedSQLiteHelper.COLUMN_ID
                    + " FROM " + RecentlyViewedSQLiteHelper.TABLE_NAME + " ORDER BY "
                    + RecentlyViewedSQLiteHelper.COLUMN_TIMESTAMP + " ASC limit (select count(*) -"
                    + (MAX_ROW_COUNT - 1) + " from " + RecentlyViewedSQLiteHelper.TABLE_NAME + " ))";
            database.execSQL(query);
        }
        long insertId = database.insert(RecentlyViewedSQLiteHelper.TABLE_NAME, null, values);

        if (-1 != insertId) {
            Cursor cursor = database.query(RecentlyViewedSQLiteHelper.TABLE_NAME,
                    allColumns, RecentlyViewedSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
    }

    public void deleteEntry(String providerId) {
        if (null != database)
            database.delete(RecentlyViewedSQLiteHelper.TABLE_NAME, RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID
                    + "=?", new String[]{providerId});
    }

    public void deleteTable() {
        if (null != database)
            database.delete(RecentlyViewedSQLiteHelper.TABLE_NAME, null, null);
    }

    @Nullable
    public String getMyEntry(String pincode) {
        Cursor myCursor = database.query(RecentlyViewedSQLiteHelper.TABLE_NAME, allColumns,
                RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID + "='" + pincode + "'", null, null, null, null);

        if (myCursor != null && myCursor.moveToFirst()) {
            if (!myCursor.isNull(1)) {
                String pin = myCursor.getString(1);
                myCursor.moveToFirst();
                myCursor.close();
                return pin;
            }
        }
        myCursor.close();
        return null;
    }

    public void updateEntry(int entryId, String providerid) {
        ContentValues values = new ContentValues();
        values.put(RecentlyViewedSQLiteHelper.COLUMN_PROVIDER_ID, providerid);
        database.update(RecentlyViewedSQLiteHelper.TABLE_NAME, values,
                RecentlyViewedSQLiteHelper.COLUMN_ID + "=?", new String[]{String.valueOf(entryId)});
    }

    private int getRowCount() {
        String countQuery = "SELECT  * FROM " + RecentlyViewedSQLiteHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}


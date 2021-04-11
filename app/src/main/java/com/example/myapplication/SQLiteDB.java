package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class SQLiteDB extends SQLiteOpenHelper {
    private static SQLiteDatabase db;
    static final int DB_VERSION = 1;
    static final String DB_NAME = "database";
    private final String TABLE_NAME_BOOK = "photobook";
    private final String PROP_ID = "id";
    private final String PROP_NAME = "name";

    final int PROP_ID_COLUMN = 0;
    final int PROP_NAME_COLUMN = 1;

    private static SQLiteDB instance;

    private SQLiteDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    public static SQLiteDB getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteDB(context, DB_NAME, null, DB_VERSION);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME_BOOK + "("
                + PROP_ID + " INTEGER PRIMARY KEY NOT NULL,"
                + PROP_NAME + " TEXT NOT NULL default ''); ";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(String[] nameArr) {
        db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < nameArr.length; i++) {
                ContentValues cv = new ContentValues();
                cv.put(PROP_NAME, nameArr[i]);
                long num = db.insertOrThrow(TABLE_NAME_BOOK, null, cv);
                if (num == -1) {
                    db.endTransaction();
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return 1;
    }

    public long insertData(String name) {
        db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(PROP_NAME, name);
        long result = db.insert(TABLE_NAME_BOOK, null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    public int updateDB(int[] idArr, String[] nameArr) {
        db = getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < idArr.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(PROP_NAME, (idArr[i] + 1) + "");
            int num = db.update(TABLE_NAME_BOOK, cv, PROP_ID + "= " + idArr[i], null);
            if (num == -1) {
                return -1;
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return 1;
    }

    public int deleteDB() {
        db = getWritableDatabase();
        db.beginTransaction();
        int num = db.delete(TABLE_NAME_BOOK, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        return num;
    }

    public ArrayList<ItemDto> selectDB() {
        db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_BOOK;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ItemDto> lists = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(PROP_ID_COLUMN);
            String name = cursor.getString(PROP_NAME_COLUMN);
            lists.add(new ItemDto(id, name));
        }
        return lists;
    }
}

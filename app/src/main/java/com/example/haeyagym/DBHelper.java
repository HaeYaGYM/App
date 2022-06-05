package com.example.haeyagym;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

interface DBContract {
    static final String TABLE_NAME="ROUTINE";
    static final String ROU_ID="ID";
    static final String ROU_NAME="NAME";
    static final String ROU_SET_COUNT="COUNT";
    static final String ROU_EXER_TIME="EXER";
    static final String ROU_BREAK="BREAK";

    static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + "(" +
            ROU_ID + " INTEGER NOT NULL PRIMARY KEY," +
            ROU_NAME + " TEXT NOT NULL, " +
            ROU_SET_COUNT + " TEXT NOT NULL, " +
            ROU_EXER_TIME + " TEXT NOT NULL, " +
            ROU_BREAK + " TEXT NOT NULL)";
    static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    static final String SQL_LOAD = "SELECT * FROM " + TABLE_NAME;
    static final String SQL_SELECT = "SELECT * FROM "  + TABLE_NAME + " WHERE " + ROU_NAME + "=?";
    static final String SQL_SELECT_ID = "SELECT ID FROM "  + TABLE_NAME + " WHERE " + ROU_NAME + "=? and " + ROU_EXER_TIME + "=? and" + ROU_BREAK + "=?";
}

class DBHelper extends SQLiteOpenHelper {
    static final String DB_FILE = "routines.db";
    static final int DB_VERSION = 1;

    DBHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION); // 세번째 인자 : cursor factory
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_TABLE);
        onCreate(db);
    }
}

package com.rdb.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface SQLiteLinstener {

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void onTableAlterByUpgrade(Class tClass, Table alterTable);
}

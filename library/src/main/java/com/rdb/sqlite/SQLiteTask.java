package com.rdb.sqlite;

import android.database.sqlite.SQLiteDatabase;

public interface SQLiteTask<T> {
    T onTaskRun(SQLiteDatabase dataBase);
}

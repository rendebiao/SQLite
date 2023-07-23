package com.rdb.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase dataBase;
    private AtomicInteger atomicInteger = new AtomicInteger();

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (dataBase != null && !dataBase.isOpen()) {
            dataBase = getWritableDatabase();
        } else if (atomicInteger.incrementAndGet() == 1) {
            dataBase = getWritableDatabase();
        }
        return dataBase;
    }

    public synchronized void closeDatabase() {
        if (atomicInteger.decrementAndGet() == 0) {
            dataBase.close();
            dataBase = null;
        }
    }
}

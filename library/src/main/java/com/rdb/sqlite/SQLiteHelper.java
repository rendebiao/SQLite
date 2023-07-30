package com.rdb.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();
    private final SQLiteLinstener sqLiteLinstener;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private SQLiteDatabase dataBase;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler, SQLiteLinstener sqLiteLinstener) {
        super(context, name, factory, version, errorHandler);
        this.sqLiteLinstener = sqLiteLinstener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLite.d(TAG, "onCreate");
        if (sqLiteLinstener != null) {
            sqLiteLinstener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLite.d(TAG, "onUpgrade");
        if (sqLiteLinstener != null) {
            sqLiteLinstener.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (dataBase != null && !dataBase.isOpen()) {
            dataBase = getWritableDatabase();
        } else if (atomicInteger.incrementAndGet() == 1) {
            dataBase = getWritableDatabase();
        }
        SQLite.e(TAG, "openDatabase " + atomicInteger.get());
        return dataBase;
    }

    public synchronized void closeDatabase() {
        if (atomicInteger.get() > 0) {
            if (atomicInteger.decrementAndGet() == 0) {
                dataBase.close();
                dataBase = null;
            }
        }
        SQLite.d(TAG, "closeDatabase " + atomicInteger.get());
    }
}

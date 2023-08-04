package com.rdb.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import java.util.concurrent.atomic.AtomicInteger;

class SQLiteOpener {

    private static final String TAG = SQLiteOpener.class.getSimpleName();
    private final SQLiteOpenHelper openHelper;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private long openCount;
    private SQLiteDatabase dataBase;
    private final Handler handler = new Handler();
    private final Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            if (atomicInteger.decrementAndGet() == 0) {
                dataBase.close();
                dataBase = null;
            }
        }
    };

    public SQLiteOpener(SQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (dataBase != null && !dataBase.isOpen()) {
            dataBase = openHelper.getWritableDatabase();
        } else if (atomicInteger.incrementAndGet() == 1) {
            dataBase = openHelper.getWritableDatabase();
        }
        openCount++;
        return dataBase;
    }

    public synchronized void closeDatabase() {
        if (atomicInteger.get() > 0) {
            if (atomicInteger.decrementAndGet() == 0) {
                dataBase.close();
                dataBase = null;
            }
        }
        SQLite.d(TAG, "openCount = " + openCount + " atomicInteger = " + atomicInteger.get());
    }

    public <T> T execSQLiteTask(SQLiteTask<T> task) {
        if (task != null) {
            SQLiteDatabase dataBase = openDatabase();
            try {
                task.onTaskRun(dataBase);
            } catch (Exception e) {
                SQLite.e(null, "execSQLiteTask", e);
            } finally {
                closeDatabase();
            }
        }
        return null;
    }
}

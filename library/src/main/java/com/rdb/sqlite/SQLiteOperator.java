package com.rdb.sqlite;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class SQLiteOperator {

    private static final String TAG = SQLiteOperator.class.getSimpleName();
    private final SQLiteOpenHelper openHelper;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private long openCount;
    private SQLiteDatabase dataBase;

    public SQLiteOperator(SQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    synchronized SQLiteDatabase openDatabase() {
        if ((atomicInteger.incrementAndGet() == 1) || (dataBase == null) || !dataBase.isOpen()) {
            dataBase = openHelper.getWritableDatabase();
        }
        openCount++;
        return dataBase;
    }

    synchronized void closeDatabase() {
        if (atomicInteger.decrementAndGet() == 0) {
            if (dataBase != null) {
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

    public boolean execSQL(String sql) {
        SQLiteDatabase dataBase = openDatabase();
        try {
            dataBase.execSQL(sql);
            SQLite.d(TAG, "execSQL=" + sql);
            return true;
        } catch (SQLException e) {
            SQLite.e(TAG, "execSQL", e);
        } finally {
            closeDatabase();
        }
        return false;
    }

    public boolean execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase dataBase = openDatabase();
        try {
            dataBase.execSQL(sql, bindArgs);
            SQLite.d(TAG, "execSQL=" + sql + " bindArgs=" + Arrays.toString(bindArgs));
            return true;
        } catch (SQLException e) {
            SQLite.e(TAG, "execSQL", e);
        } finally {
            closeDatabase();
        }
        return false;
    }

    public boolean alterTable(String tableName, String newTableName) {
        if (!TextUtils.isEmpty(tableName) && !TextUtils.isEmpty(newTableName)) {
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + newTableName;
            return execSQL(sql);
        }
        return false;
    }

    public boolean dropTable(String tableName) {
        if (!TextUtils.isEmpty(tableName)) {
            String sql = "DROP TABLE IF EXISTS " + tableName;
            return execSQL(sql);
        }
        return false;
    }

    public boolean isTableExists(String tableName) {
        boolean exists = false;
        if (!TextUtils.isEmpty(tableName)) {
            SQLiteDatabase dataBase = openDatabase();
            String sql = "SELECT name FROM sqlite_master WHERE type = 'table'";
            try {
                Cursor cursor = dataBase.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    if (tableName.equals(name)) {
                        exists = true;
                        break;
                    }
                }
            } catch (Exception e) {
                SQLite.e(TAG, "getTableNames", e);
            } finally {
                closeDatabase();
            }
        }
        return exists;
    }

    public List<String> getTableNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase dataBase = openDatabase();
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table'";
        try {
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                names.add(name);
            }
        } catch (Exception e) {
            SQLite.e(TAG, "getTableNames", e);
        } finally {
            closeDatabase();
        }
        return names;
    }
}

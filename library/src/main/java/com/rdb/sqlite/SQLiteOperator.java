package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

class SQLiteOperator {

    private static final String TAG = SQLiteOperator.class.getSimpleName();
    private final SQLiteOpenHelper openHelper;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private EntityConverter converter;
    private long openCount;
    private SQLiteDatabase dataBase;

    public SQLiteOperator(SQLiteOpenHelper openHelper) {
        this.openHelper = openHelper;
    }

    public void setConverter(EntityConverter converter) {
        this.converter = converter;
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

    public boolean insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase dataBase = openDatabase();
        long insert = dataBase.insert(tableName, null, contentValues);
        if (insert > 0) {
            SQLite.d(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + contentValues);
        } else {
            SQLite.e(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + contentValues);
        }
        closeDatabase();
        return insert > 0;
    }

    public boolean replace(String tableName, ContentValues contentValues) {
        SQLiteDatabase dataBase = openDatabase();
        long replace = dataBase.replace(tableName, null, contentValues);
        if (replace > 0) {
            SQLite.d(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + contentValues);
        } else {
            SQLite.e(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + contentValues);
        }
        closeDatabase();
        return replace > 0;
    }

    public boolean update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = openDatabase();
        int update = dataBase.update(tableName, contentValues, whereClause, whereArgs);
        if (update > 0) {
            SQLite.d(TAG, "update contentValues to " + tableName + " (" + update + ") " + contentValues);
        } else {
            SQLite.e(TAG, "update contentValues to " + tableName + " (" + update + ") " + contentValues);
        }
        closeDatabase();
        return update > 0;
    }

    public boolean delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = openDatabase();
        int delete = dataBase.delete(tableName, whereClause, whereArgs);
        if (delete > 0) {
            SQLite.d(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        } else {
            SQLite.e(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        }
        closeDatabase();
        return delete > 0;
    }

    public <T> boolean insert(String tableName, T object) {
        SQLiteDatabase dataBase = openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long insert = dataBase.insert(tableName, null, valuesPutter.getContentValues());
        if (insert > 0) {
            SQLite.d(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        }
        closeDatabase();
        return insert > 0;
    }

    public <T> boolean replace(String tableName, T object) {
        SQLiteDatabase dataBase = openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long replace = dataBase.replace(tableName, null, valuesPutter.getContentValues());
        if (replace > 0) {
            SQLite.d(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        }
        closeDatabase();
        return replace > 0;
    }

    public void query(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CursorReader cursorReader) {
        SQLiteDatabase dataBase = openDatabase();
        Cursor cursor = dataBase.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        cursorReader.onReadCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        closeDatabase();
    }

    public <T> T queryObject(String tableName, String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = objectReader.readColumn(cursor.getPosition(), valuesGetter);
            }
            cursor.close();
        }
        closeDatabase();
        return object;
    }

    public <T> ArrayList<T> queryList(boolean distinct, String tableName, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = dataBase.query(distinct, tableName, null, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            while (cursor.moveToNext()) {
                T t = objectReader.readColumn(cursor.getPosition(), valuesGetter);
                if (t != null) {
                    objects.add(t);
                }
            }
            cursor.close();
        }
        closeDatabase();
        return objects;
    }

    public <T> T queryObject(Class<T> tClass, String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase dataBase = openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = EntityUtil.newObject(tClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                }
            }
            cursor.close();
        }
        closeDatabase();
        return object;
    }

    public <T> ArrayList<T> queryList(Class<T> tClass, boolean distinct, String tableName, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase dataBase = openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = dataBase.query(distinct, tableName, null, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor != null) {
            T object = null;
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            while (cursor.moveToNext()) {
                object = EntityUtil.newObject(tClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                    objects.add(object);
                }
            }
            cursor.close();
        }
        closeDatabase();
        return objects;
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
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                if (tableName.equals(name)) {
                    exists = true;
                    break;
                }
            }
            closeDatabase();
        }
        return exists;
    }
}

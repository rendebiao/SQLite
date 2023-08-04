package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public final class Table {

    private static final String TAG = Table.class.getSimpleName();
    private final String tableName;
    private final SQLiteOperator operator;

    Table(SQLiteOperator operator, String tableName) {
        this.operator = operator;
        this.tableName = tableName;
    }

    public boolean insert(ContentValues values) {
        SQLiteDatabase dataBase = operator.openDatabase();
        long insert = dataBase.insert(tableName, null, values);
        if (insert > 0) {
            SQLite.d(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + values);
        } else {
            SQLite.e(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + values);
        }
        operator.closeDatabase();
        return insert > 0;
    }

    public boolean replace(ContentValues values) {
        SQLiteDatabase dataBase = operator.openDatabase();
        long replace = dataBase.replace(tableName, null, values);
        if (replace > 0) {
            SQLite.d(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + values);
        } else {
            SQLite.e(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + values);
        }
        operator.closeDatabase();
        return replace > 0;
    }

    public boolean update(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = operator.openDatabase();
        int update = dataBase.update(tableName, values, whereClause, whereArgs);
        if (update > 0) {
            SQLite.d(TAG, "update contentValues to " + tableName + " (" + update + ") " + values);
        } else {
            SQLite.e(TAG, "update contentValues to " + tableName + " (" + update + ") " + values);
        }
        operator.closeDatabase();
        return update > 0;
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = operator.openDatabase();
        int delete = dataBase.delete(tableName, whereClause, whereArgs);
        if (delete > 0) {
            SQLite.d(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        } else {
            SQLite.e(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        }
        operator.closeDatabase();
        return delete > 0;
    }

    public <T> T queryObject(String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = operator.openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = objectReader.readColumn(cursor.getPosition(), valuesGetter);
            }
            cursor.close();
        }
        operator.closeDatabase();
        return object;
    }

    public void queryAll(CursorReader cursorReader) {
        query(false, null, "1=1", null, null, null, null, null, cursorReader);
    }

    public void queryAll(String orderBy, String limit, CursorReader cursorReader) {
        query(false, null, "1=1", null, null, null, orderBy, limit, cursorReader);
    }

    public void query(String[] columns, String selection, String[] selectionArgs, CursorReader cursorReader) {
        query(false, columns, selection, selectionArgs, null, null, null, null, cursorReader);
    }

    public void query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorReader cursorReader) {
        query(false, columns, selection, selectionArgs, groupBy, having, orderBy, null, cursorReader);
    }

    public void query(boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CursorReader cursorReader) {
        SQLiteDatabase dataBase = operator.openDatabase();
        Cursor cursor = dataBase.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        cursorReader.onReadCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        operator.closeDatabase();
    }

    public <T> ArrayList<T> queryAll(ObjectReader<T> objectReader) {
        return queryList(false, "1=1", null, null, null, null, null, objectReader);
    }

    public <T> ArrayList<T> queryAll(String orderBy, String limit, ObjectReader<T> objectReader) {
        return queryList(false, "1=1", null, null, null, orderBy, limit, objectReader);
    }

    public <T> ArrayList<T> queryList(String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        return queryList(false, selection, selectionArgs, null, null, null, null, objectReader);
    }

    public <T> ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, ObjectReader<T> objectReader) {
        return queryList(false, selection, selectionArgs, groupBy, having, orderBy, null, objectReader);
    }

    public <T> ArrayList<T> queryList(boolean distinct, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = operator.openDatabase();
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
        operator.closeDatabase();
        return objects;
    }
}

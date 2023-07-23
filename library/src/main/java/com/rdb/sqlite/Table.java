package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Table {

    protected final String tableName;
    protected final SQLiteHelper helper;

    public Table(SQLiteHelper helper, String tableName) {
        this.helper = helper;
        this.tableName = tableName;
    }

    public final boolean insert(ContentValues contentValues) {
        boolean insert = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            insert = dataBase.insert(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return insert;
    }

    public final boolean replace(ContentValues contentValues) {
        boolean replace = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            replace = dataBase.replace(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return replace;
    }

    public boolean update(ContentValues values, String whereClause, String[] whereArgs) {
        boolean update = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            update = dataBase.update(tableName, values, whereClause, whereArgs) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return update;
    }

    public <T> boolean insert(T object, ObjectConverter<T> objectConverter) {
        boolean insert = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValues contentValues = objectConverter.convertObject(object);
        try {
            insert = dataBase.insert(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return insert;
    }

    public <T> boolean replace(T object, ObjectConverter<T> objectConverter) {
        boolean replace = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValues contentValues = objectConverter.convertObject(object);
        try {
            replace = dataBase.replace(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return replace;
    }

    public final boolean delete(String whereClause, String[] whereArgs) {
        int delete = 0;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            delete = dataBase.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return delete > 0;
    }

    public final <T> T queryObject(String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        T object = null;
        SQLiteDatabase dataBase = helper.openDatabase();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    object = converter.convertCursor(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        helper.closeDatabase();
        return object;
    }

    public final <T> ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        return queryList(columns, selection, selectionArgs, null, null, null, converter);
    }

    public final <T> ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorConverter<T> converter) {
        SQLiteDatabase dataBase = helper.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    objects.add(converter.convertCursor(cursor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        helper.closeDatabase();
        return objects;
    }

    public final void execSQL(String sql) {
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            dataBase.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
    }

    public final void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            dataBase.execSQL(sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
    }

    public void dropTable() {
        SQLiteDatabase dataBase = helper.openDatabase();
        dataBase.execSQL("DROP TABLE IF EXISTS " + tableName);
        helper.closeDatabase();
    }

    public SQLiteHelper getHelper() {
        return helper;
    }

    public interface CursorConverter<T> {
        T convertCursor(Cursor cursor);
    }

    public interface ObjectConverter<T> {
        ContentValues convertObject(T object);
    }
}

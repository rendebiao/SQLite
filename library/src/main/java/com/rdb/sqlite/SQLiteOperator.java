package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

class SQLiteOperator {

    private static final String TAG = SQLiteOperator.class.getSimpleName();
    private final SQLiteOpener opener;
    private EntityConverter converter;

    public SQLiteOperator(SQLiteOpener opener) {
        this.opener = opener;
    }

    public void setConverter(EntityConverter converter) {
        this.converter = converter;
    }

    public boolean insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase dataBase = opener.openDatabase();
        long insert = dataBase.insert(tableName, null, contentValues);
        if (insert > 0) {
            SQLite.d(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + contentValues);
        } else {
            SQLite.e(TAG, "insert contentValues to " + tableName + " (" + insert + ") " + contentValues);
        }
        opener.closeDatabase();
        return insert > 0;
    }

    public boolean replace(String tableName, ContentValues contentValues) {
        SQLiteDatabase dataBase = opener.openDatabase();
        long replace = dataBase.replace(tableName, null, contentValues);
        if (replace > 0) {
            SQLite.d(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + contentValues);
        } else {
            SQLite.e(TAG, "replace contentValues to " + tableName + " (" + replace + ") " + contentValues);
        }
        opener.closeDatabase();
        return replace > 0;
    }

    public boolean update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = opener.openDatabase();
        int update = dataBase.update(tableName, contentValues, whereClause, whereArgs);
        if (update > 0) {
            SQLite.d(TAG, "update contentValues to " + tableName + " (" + update + ") " + contentValues);
        } else {
            SQLite.e(TAG, "update contentValues to " + tableName + " (" + update + ") " + contentValues);
        }
        opener.closeDatabase();
        return update > 0;
    }

    public boolean delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase dataBase = opener.openDatabase();
        int delete = dataBase.delete(tableName, whereClause, whereArgs);
        if (delete > 0) {
            SQLite.d(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        } else {
            SQLite.e(TAG, "delete from " + tableName + " (" + delete + ") " + whereClause + " " + Arrays.toString(whereArgs));
        }
        opener.closeDatabase();
        return delete > 0;
    }

    public <T> boolean insert(String tableName, T object) {
        SQLiteDatabase dataBase = opener.openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long insert = dataBase.insert(tableName, null, valuesPutter.getContentValues());
        if (insert > 0) {
            SQLite.d(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        }
        opener.closeDatabase();
        return insert > 0;
    }

    public <T> boolean replace(String tableName, T object) {
        SQLiteDatabase dataBase = opener.openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long replace = dataBase.replace(tableName, null, valuesPutter.getContentValues());
        if (replace > 0) {
            SQLite.d(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        }
        opener.closeDatabase();
        return replace > 0;
    }

    public void queryAll(String tableName, CursorReader cursorReader) {
        query(tableName, null, "1=1", null, null, null, null, cursorReader);
    }

    public void query(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorReader cursorReader) {
        SQLiteDatabase dataBase = opener.openDatabase();
        Cursor cursor = dataBase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
        cursorReader.onReadCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        opener.closeDatabase();
    }

    public <T> T queryObject(String tableName, String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = opener.openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = objectReader.readColumn(cursor.getPosition(), valuesGetter);
            }
            cursor.close();
        }
        opener.closeDatabase();
        return object;
    }

    public <T> ArrayList<T> queryAll(String tableName, ObjectReader<T> objectReader) {
        return queryList(tableName, "1=1", null, null, null, null, objectReader);
    }

    public <T> ArrayList<T> queryList(String tableName, String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        return queryList(tableName, selection, selectionArgs, null, null, null, objectReader);
    }

    public <T> ArrayList<T> queryList(String tableName, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, ObjectReader<T> objectReader) {
        SQLiteDatabase dataBase = opener.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, groupBy, having, orderBy);
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
        opener.closeDatabase();
        return objects;
    }

    public <T> T queryObject(Class<T> tClass, String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase dataBase = opener.openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = Entity.newObject(tClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                }
            }
            cursor.close();
        }
        opener.closeDatabase();
        return object;
    }

    public <T> ArrayList<T> queryAll(Class<T> tClass, String tableName) {
        return queryList(tClass, tableName, "1=1", null, null, null, null);
    }

    public <T> ArrayList<T> queryList(Class<T> tClass, String tableName, String selection, String[] selectionArgs) {
        return queryList(tClass, tableName, selection, selectionArgs, null, null, null);
    }

    public <T> ArrayList<T> queryList(Class<T> tClass, String tableName, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase dataBase = opener.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, groupBy, having, orderBy);
        if (cursor != null) {
            T object = null;
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            while (cursor.moveToNext()) {
                object = Entity.newObject(tClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                    objects.add(object);
                }
            }
            cursor.close();
        }
        opener.closeDatabase();
        return objects;
    }

    public boolean execSQL(String sql) {
        SQLiteDatabase dataBase = opener.openDatabase();
        try {
            dataBase.execSQL(sql);
            SQLite.d(TAG, "execSQL=" + sql);
            return true;
        } catch (SQLException e) {
            SQLite.e(TAG, "execSQL", e);
        } finally {
            opener.closeDatabase();
        }
        return false;
    }

    public boolean execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase dataBase = opener.openDatabase();
        try {
            dataBase.execSQL(sql, bindArgs);
            SQLite.d(TAG, "execSQL=" + sql + " bindArgs=" + Arrays.toString(bindArgs));
            return true;
        } catch (SQLException e) {
            SQLite.e(TAG, "execSQL", e);
        } finally {
            opener.closeDatabase();
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
            SQLiteDatabase dataBase = opener.openDatabase();
            String sql = "SELECT name FROM sqlite_master WHERE type = 'table'";
            Cursor cursor = dataBase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                if (tableName.equals(name)) {
                    exists = true;
                    break;
                }
            }
            opener.closeDatabase();
        }
        return exists;
    }
}

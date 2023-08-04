package com.rdb.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public final class EntityTable<T> {

    private static final String TAG = EntityTable.class.getSimpleName();
    private final Class<T> entityClass;
    private final String tableName;
    private final SQLiteOperator operator;
    private final EntityConverter converter;

    EntityTable(SQLiteOperator operator, Class<T> entityClass, String tableName, EntityConverter converter) {
        this.operator = operator;
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.converter = converter;
    }

    public boolean insert(T object) {
        SQLiteDatabase dataBase = operator.openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long insert = dataBase.insert(tableName, null, valuesPutter.getContentValues());
        if (insert > 0) {
            SQLite.d(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "insert object to " + tableName + " (" + insert + ") " + valuesPutter.getContentValues());
        }
        operator.closeDatabase();
        return insert > 0;
    }

    public boolean replace(T object) {
        SQLiteDatabase dataBase = operator.openDatabase();
        ValuesPutter valuesPutter = new ValuesPutter();
        converter.convert(object, valuesPutter);
        long replace = dataBase.replace(tableName, null, valuesPutter.getContentValues());
        if (replace > 0) {
            SQLite.d(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        } else {
            SQLite.e(TAG, "replace object to " + tableName + " (" + replace + ") " + valuesPutter.getContentValues());
        }
        operator.closeDatabase();
        return replace > 0;
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

    public T queryObject(String selection, String[] selectionArgs) {
        SQLiteDatabase dataBase = operator.openDatabase();
        T object = null;
        Cursor cursor = dataBase.query(tableName, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            if (cursor.moveToFirst()) {
                object = EntityUtil.newObject(entityClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                }
            }
            cursor.close();
        }
        operator.closeDatabase();
        return object;
    }

    public ArrayList<T> queryAll() {
        return queryList(false, "1=1", null, null, null, null, null);
    }


    public ArrayList<T> queryAll(String orderBy, String limit) {
        return queryList(false, "1=1", null, null, null, orderBy, limit);
    }

    public ArrayList<T> queryList(String selection, String[] selectionArgs) {
        return queryList(false, selection, selectionArgs, null, null, null, null);
    }

    public ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return queryList(false, selection, selectionArgs, groupBy, having, orderBy, null);
    }

    public ArrayList<T> queryList(boolean distinct, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase dataBase = operator.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = dataBase.query(distinct, tableName, null, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor != null) {
            T object = null;
            ValuesGetter valuesGetter = new ValuesGetter(cursor);
            while (cursor.moveToNext()) {
                object = EntityUtil.newObject(entityClass);
                if (object != null) {
                    converter.convert(object, valuesGetter);
                    objects.add(object);
                }
            }
            cursor.close();
        }
        operator.closeDatabase();
        return objects;
    }
}

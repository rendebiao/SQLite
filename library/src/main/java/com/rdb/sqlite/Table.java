package com.rdb.sqlite;

import android.content.ContentValues;

import com.rdb.sqlite.converter.CursorConverter;
import com.rdb.sqlite.converter.ObjectConverter;

import java.util.ArrayList;

public class Table {

    protected final String tableName;
    protected final SQLiteHelper helper;

    Table(SQLiteHelper helper, String tableName) {
        this.helper = helper;
        this.tableName = tableName;
    }

    public final boolean insert(ContentValues values) {
        return SQLiteOperator.insert(helper, tableName, values);
    }

    public final boolean replace(ContentValues values) {
        return SQLiteOperator.replace(helper, tableName, values);
    }

    public boolean update(ContentValues values, String whereClause, String[] whereArgs) {
        return SQLiteOperator.update(helper, tableName, values, whereClause, whereArgs);
    }

    public <T> boolean insert(T object, ObjectConverter<T> objectConverter) {
        return SQLiteOperator.insert(helper, tableName, object, objectConverter);
    }

    public <T> boolean replace(T object, ObjectConverter<T> objectConverter) {
        return SQLiteOperator.replace(helper, tableName, object, objectConverter);
    }

    public final boolean delete(String whereClause, String[] whereArgs) {
        return SQLiteOperator.delete(helper, tableName, whereClause, whereArgs);
    }

    public final <T> T queryObject(String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        return SQLiteOperator.queryObject(helper, tableName, columns, selection, selectionArgs, converter);
    }

    public final <T> ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        return SQLiteOperator.queryList(helper, tableName, columns, selection, selectionArgs, converter);
    }

    public final <T> ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorConverter<T> converter) {
        return SQLiteOperator.queryList(helper, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, converter);
    }
}

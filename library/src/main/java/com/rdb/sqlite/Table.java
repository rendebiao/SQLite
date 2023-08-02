package com.rdb.sqlite;

import android.content.ContentValues;

import java.util.ArrayList;

public final class Table {

    private final String tableName;
    private final SQLiteOperator operator;

    Table(SQLiteOperator operator, String tableName) {
        this.operator = operator;
        this.tableName = tableName;
    }

    public boolean insert(ContentValues values) {
        return operator.insert(tableName, values);
    }

    public boolean replace(ContentValues values) {
        return operator.replace(tableName, values);
    }

    public boolean update(ContentValues values, String whereClause, String[] whereArgs) {
        return operator.update(tableName, values, whereClause, whereArgs);
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        return operator.delete(tableName, whereClause, whereArgs);
    }

    public void queryAll(CursorReader cursorReader) {
        operator.queryAll(tableName, cursorReader);
    }

    public void query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorReader cursorReader) {
        operator.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, cursorReader);
    }

    public <T> T queryObject(String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        return operator.queryObject(tableName, selection, selectionArgs, objectReader);
    }

    public <T> ArrayList<T> queryAll(ObjectReader<T> objectReader) {
        return operator.queryAll(tableName, objectReader);
    }

    public <T> ArrayList<T> queryList(String selection, String[] selectionArgs, ObjectReader<T> objectReader) {
        return operator.queryList(tableName, selection, selectionArgs, objectReader);
    }

    public <T> ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, ObjectReader<T> objectReader) {
        return operator.queryList(tableName, selection, selectionArgs, groupBy, having, orderBy, objectReader);
    }
}

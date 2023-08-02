package com.rdb.sqlite;

import java.util.ArrayList;

public final class EntityTable {

    private final String tableName;
    private final SQLiteOperator operator;

    EntityTable(SQLiteOperator operator, String tableName) {
        this.operator = operator;
        this.tableName = tableName;
    }

    public <T> boolean insert(T object) {
        return operator.insert(tableName, object);
    }

    public <T> boolean replace(T object) {
        return operator.replace(tableName, object);
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        return operator.delete(tableName, whereClause, whereArgs);
    }

    public <T> T queryObject(Class<T> tClass, String selection, String[] selectionArgs) {
        return operator.queryObject(tClass, tableName, selection, selectionArgs);
    }

    public <T> ArrayList<T> queryAll(Class<T> tClass) {
        return operator.queryAll(tClass, tableName);
    }

    public <T> ArrayList<T> queryList(Class<T> tClass, String selection, String[] selectionArgs) {
        return operator.queryList(tClass, tableName, selection, selectionArgs);
    }

    public <T> ArrayList<T> queryList(Class<T> tClass, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return operator.queryList(tClass, tableName, selection, selectionArgs, groupBy, having, orderBy);
    }
}

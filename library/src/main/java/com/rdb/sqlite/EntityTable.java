package com.rdb.sqlite;

import java.util.ArrayList;

public final class EntityTable<T> {

    private final Class<T> entityClass;
    private final String tableName;
    private final SQLiteOperator operator;

    EntityTable(SQLiteOperator operator, Class<T> entityClass, String tableName) {
        this.operator = operator;
        this.entityClass = entityClass;
        this.tableName = tableName;
    }

    public boolean insert(T object) {
        return operator.insert(tableName, object);
    }

    public boolean replace(T object) {
        return operator.replace(tableName, object);
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        return operator.delete(tableName, whereClause, whereArgs);
    }

    public T queryObject(String selection, String[] selectionArgs) {
        return operator.queryObject(entityClass, tableName, selection, selectionArgs);
    }

    public ArrayList<T> queryAll() {
        return operator.queryList(entityClass, false, tableName, "1=1", null, null, null, null, null);
    }


    public ArrayList<T> queryAll(String orderBy, String limit) {
        return operator.queryList(entityClass, false, tableName, "1=1", null, null, null, orderBy, limit);
    }

    public ArrayList<T> queryList(String selection, String[] selectionArgs) {
        return operator.queryList(entityClass, false, tableName, selection, selectionArgs, null, null, null, null);
    }

    public ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return operator.queryList(entityClass, false, tableName, selection, selectionArgs, groupBy, having, orderBy, null);
    }

    public ArrayList<T> queryList(boolean distinct, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return operator.queryList(entityClass, distinct, tableName, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}

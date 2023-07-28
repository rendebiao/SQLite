package com.rdb.sqlite;

import java.util.ArrayList;

public class EntityTable<T> {

    private final Class<T> tClass;
    private final String tableName;
    private final SQLiteHelper helper;
    private final EntityConverter<T> entityConverter;

    EntityTable(SQLiteHelper helper, final Class<T> tClass) {
        tableName = Entity.getTableName(tClass);
        this.tClass = tClass;
        this.helper = helper;
        if (tClass == null) {
            throw new RuntimeException("tClass == null");
        }
        if (!Entity.haveEmptyConstructor(tClass)) {
            throw new RuntimeException(tClass + " has not empty constructor");
        }
        this.entityConverter = new EntityConverter<>(tClass);
    }

    public boolean insert(T object) {
        return SQLiteOperator.insert(helper, tableName, object, entityConverter);
    }

    public boolean replace(T object) {
        return SQLiteOperator.replace(helper, tableName, object, entityConverter);
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        return SQLiteOperator.delete(helper, tableName, whereClause, whereArgs);
    }

    public final T queryObject(String selection, String[] selectionArgs) {
        return SQLiteOperator.queryObject(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, entityConverter);
    }

    public final ArrayList<T> queryList(String selection, String[] selectionArgs) {
        return SQLiteOperator.queryList(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, entityConverter);
    }

    public final ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return SQLiteOperator.queryList(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, groupBy, having, orderBy, entityConverter);
    }
}

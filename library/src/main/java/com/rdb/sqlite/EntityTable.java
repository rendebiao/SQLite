package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.converter.CursorConverter;
import com.rdb.sqlite.converter.ObjectConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityTable<T> {

    private Class<T> tClass;
    private String tableName;
    private SQLiteHelper helper;
    private ObjectConverter<T> objectConverter;
    private CursorConverter<T> cursorConverter;

    EntityTable(SQLiteHelper helper, final Class<T> tClass) {
        tableName = EntityUtils.getTableName(tClass);
        this.tClass = tClass;
        this.helper = helper;
        if (tClass == null) {
            throw new RuntimeException("tClass == null");
        }
        if (!EntityUtils.hasEmptyConstructor(tClass)) {
            throw new RuntimeException(tClass + " has not empty constructor");
        }
        this.objectConverter = new ObjectConverter<T>() {
            @Override
            public ContentValues convertObject(T object) {
                return EntityTable.convertObject(object, tClass);
            }
        };
        this.cursorConverter = new CursorConverter<T>() {
            @Override
            public T convertCursor(Cursor cursor) {
                return EntityTable.convertCursor(cursor, tClass);
            }
        };
    }

    public static <T> T convertCursor(Cursor cursor, Class<T> tClass) {
        T object = null;
        try {
            object = tClass.newInstance();
            List<Field> fields = EntityUtils.getUnStaticDeclaredFields(tClass);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (EntityUtils.isColumn(field) && EntityUtils.support(field)) {
                    EntityUtils.cursorToFieldValue(cursor, object, field);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static <T> ContentValues convertObject(T object, Class<T> tClass) {
        List<Field> fields = EntityUtils.getUnStaticDeclaredFields(tClass);
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            EntityColumn column = EntityUtils.getColumn(field);
            if (column == null || !column.hide()) {
                EntityUtils.fieldValueToContentValues(contentValues, object, field, column != null && column.autoIncrement());
            }
        }
        return contentValues;
    }

    public boolean insert(T object) {
        return SQLiteOperator.insert(helper, tableName, object, objectConverter);
    }

    public boolean replace(T object) {
        return SQLiteOperator.replace(helper, tableName, object, objectConverter);
    }

    public boolean delete(String whereClause, String[] whereArgs) {
        return SQLiteOperator.delete(helper, tableName, whereClause, whereArgs);
    }

    public final T queryObject(String[] columns, String selection, String[] selectionArgs) {
        return SQLiteOperator.queryObject(helper, tableName, columns, selection, selectionArgs, cursorConverter);
    }

    public final ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs) {
        return SQLiteOperator.queryList(helper, tableName, columns, selection, selectionArgs, cursorConverter);
    }

    public final ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return SQLiteOperator.queryList(helper, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, cursorConverter);
    }
}

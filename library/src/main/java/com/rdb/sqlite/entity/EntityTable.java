package com.rdb.sqlite.entity;

import android.content.ContentValues;
import android.database.Cursor;

import com.rdb.sqlite.Table;
import com.rdb.sqlite.entity.annotation.EntityColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityTable<T> extends Table {

    private Class<T> tClass;
    private EntityHelper entityHelper;
    private ObjectConverter<T> objectConverter;
    private CursorConverter<T> cursorConverter;

    public EntityTable(EntityHelper entityHelper, final Class<T> tClass) {
        super(entityHelper, Entity.getTableName(tClass));
        this.tClass = tClass;
        this.entityHelper = entityHelper;
        if (tClass == null) {
            throw new RuntimeException("tClass == null");
        }
        if (!Entity.hasEmptyConstructor(tClass)) {
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
        entityHelper.addTable(tClass);
    }

    public static <T> T convertCursor(Cursor cursor, Class<T> tClass) {
        T object = null;
        try {
            object = tClass.newInstance();
            List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (Entity.isColumn(field) && Entity.support(field)) {
                    Entity.cursorToFieldValue(cursor, object, field);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static <T> ContentValues convertObject(T object, Class<T> tClass) {
        List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            EntityColumn column = Entity.getColumn(field);
            if (column == null || !column.hide()) {
                Entity.fieldValueToContentValues(contentValues, object, field, column != null && column.autoIncrement());
            }
        }
        return contentValues;
    }

    public boolean insert(T object) {
        return insert(object, objectConverter);
    }

    public boolean replace(T object) {
        return replace(object, objectConverter);
    }

    public final T queryObject(String[] columns, String selection, String[] selectionArgs) {
        return queryObject(columns, selection, selectionArgs, cursorConverter);
    }

    public final ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs) {
        return queryList(columns, selection, selectionArgs, null, null, null);
    }

    public final ArrayList<T> queryList(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return queryList(columns, selection, selectionArgs, groupBy, having, orderBy, cursorConverter);
    }

    @Override
    public void dropTable() {
        super.dropTable();
        entityHelper.delTable(tClass);
    }
}

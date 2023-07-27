package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.converter.CursorConverter;
import com.rdb.sqlite.converter.EntityColumnConverter;
import com.rdb.sqlite.converter.ObjectConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityTable<T> {

    private final Class<T> tClass;
    private final String tableName;
    private final SQLiteHelper helper;
    private final ObjectConverter<T> objectConverter;
    private final CursorConverter<T> cursorConverter;

    EntityTable(SQLiteHelper helper, final Class<T> tClass) {
        tableName = Entity.getTableName(tClass);
        this.tClass = tClass;
        this.helper = helper;
        if (tClass == null) {
            throw new RuntimeException("tClass == null");
        }
        if (!Entity.hasEmptyConstructor(tClass)) {
            throw new RuntimeException(tClass + " has not empty constructor");
        }
        this.objectConverter = new EntityObjectConverter<>(tClass);
        this.cursorConverter = new EntityCursorConverter<>(tClass);
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

    public final T queryObject(String selection, String[] selectionArgs) {
        return SQLiteOperator.queryObject(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, cursorConverter);
    }

    public final ArrayList<T> queryList(String selection, String[] selectionArgs) {
        return SQLiteOperator.queryList(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, cursorConverter);
    }

    public final ArrayList<T> queryList(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return SQLiteOperator.queryList(helper, tableName, Entity.getColumnNames(tClass), selection, selectionArgs, groupBy, having, orderBy, cursorConverter);
    }

    class EntityCursorConverter<T> implements CursorConverter<T> {

        private final Class<T> tClass;

        public EntityCursorConverter(Class<T> tClass) {
            this.tClass = tClass;
        }

        public T convertCursor(Cursor cursor) {
            T object = null;
            try {
                object = tClass.newInstance();
                List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    if (Entity.isColumn(field)) {
                        cursorToFieldValue(cursor, object, field);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }

        private void cursorToFieldValue(Cursor cursor, Object obj, Field field) {
            try {
                field.setAccessible(true);
                int columnIndex = cursor.getColumnIndex(field.getName());
                if (columnIndex >= 0) {
                    EntityColumnConverter converter = Entity.getEntityColumnConverter(field.getType().getName());
                    if ((converter == null) || cursor.isNull(columnIndex)) {
                        field.set(obj, null);
                    } else {
                        converter.cursorToFieldValue(cursor, columnIndex, obj, field);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class EntityObjectConverter<T> implements ObjectConverter<T> {

        private final Class<T> tClass;

        public EntityObjectConverter(Class<T> tClass) {
            this.tClass = tClass;
        }

        public ContentValues convertObject(T object) {
            List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                EntityColumn column = field.getAnnotation(EntityColumn.class);
                if (column == null || (!column.hide() && !column.autoIncrement())) {
                    fieldValueToContentValues(contentValues, object, field, column != null && column.autoIncrement());
                }
            }
            return contentValues;
        }

        private void fieldValueToContentValues(ContentValues contentValues, Object obj, Field field, boolean autoIncrement) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                EntityColumnConverter converter = Entity.getEntityColumnConverter(field.getType().getName());
                if (converter == null || fieldValue == null || (autoIncrement && fieldValue == Integer.valueOf(0))) {
                    contentValues.putNull(field.getName());
                } else {
                    converter.fieldValueToContentValues(contentValues, field.getName(), fieldValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

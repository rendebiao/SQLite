package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.annotation.EntityVersion;
import com.rdb.sqlite.converter.EntityColumnConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EntityUtils {

    private static Map<String, EntityColumnConverter> converters = new HashMap<>();

    static {
        converters.put(Boolean.TYPE.getName(), EntityColumnConverter.BOOLEAN_CONVERTER);
        converters.put(Boolean.class.getName(), EntityColumnConverter.BOOLEAN_CONVERTER);
        converters.put(byte[].class.getName(), EntityColumnConverter.BYTES_CONVERTER);
        converters.put(Character.TYPE.getName(), EntityColumnConverter.CHAR_CONVERTER);
        converters.put(Character.class.getName(), EntityColumnConverter.CHAR_CONVERTER);
        converters.put(Byte.TYPE.getName(), EntityColumnConverter.BYTE_CONVERTER);
        converters.put(Byte.class.getName(), EntityColumnConverter.BYTE_CONVERTER);
        converters.put(Short.TYPE.getName(), EntityColumnConverter.SHORT_CONVERTER);
        converters.put(Short.class.getName(), EntityColumnConverter.SHORT_CONVERTER);
        converters.put(Integer.TYPE.getName(), EntityColumnConverter.INTEGER_CONVERTER);
        converters.put(Integer.class.getName(), EntityColumnConverter.INTEGER_CONVERTER);
        converters.put(Long.TYPE.getName(), EntityColumnConverter.LONG_CONVERTER);
        converters.put(Long.class.getName(), EntityColumnConverter.LONG_CONVERTER);
        converters.put(Float.TYPE.getName(), EntityColumnConverter.FLOAT_CONVERTER);
        converters.put(Float.class.getName(), EntityColumnConverter.FLOAT_CONVERTER);
        converters.put(Double.TYPE.getName(), EntityColumnConverter.DOUBLE_CONVERTER);
        converters.put(Double.class.getName(), EntityColumnConverter.DOUBLE_CONVERTER);
        converters.put(String.class.getName(), EntityColumnConverter.STRING_CONVERTER);
    }

    public static void registerConverter(String type, EntityColumnConverter columnConverter) {
        converters.put(type, columnConverter);
    }

    static void log(String msg) {
        Log.e("SQLite", msg);
    }

    static boolean support(Field field) {
        return converters.containsKey(field.getType().getName());
    }

    private static String columnType(Field field) {
        EntityColumnConverter columnConverter = converters.get(field.getType().getName());
        return columnConverter == null ? null : columnConverter.getColumnType();
    }

    static void cursorToFieldValue(Cursor cursor, Object obj, Field field) {
        try {
            field.setAccessible(true);
            int columnIndex = cursor.getColumnIndex(field.getName());
            if (columnIndex >= 0) {
                if (cursor.isNull(columnIndex)) {
                    field.set(obj, null);
                } else {
                    converters.get(field.getType().getName()).cursorToFieldValue(cursor, columnIndex, obj, field);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void fieldValueToContentValues(ContentValues contentValues, Object obj, Field field, boolean autoIncrement) {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            if (fieldValue == null || (autoIncrement && fieldValue == Integer.valueOf(0))) {
                contentValues.putNull(field.getName());
            } else {
                converters.get(field.getType().getName()).fieldValueToContentValues(contentValues, field.getName(), fieldValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTableName(Class tClass) {
        return tClass.getName().replace(".", "_");
    }

    public static String getCreateTableSQL(Class tClass) {
        List<Field> fields = getUnStaticDeclaredFields(tClass);
        TableSQLBuilder builder = new TableSQLBuilder(getTableName(tClass));
        for (int i = 0; i < fields.size(); i++) {
            Column column = getFieldColumn(fields.get(i));
            if (column != null) {
                builder.addColumn(column);
            }
        }
        return builder.build();
    }

    public static List<Field> getUnStaticDeclaredFields(Class tClass) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                fieldList.add(fields[i]);
            }
        }
        return fieldList;
    }

    public static String getDropTableSQL(Class tClass) {
        return "DROP TABLE IF EXISTS " + getTableName(tClass);
    }

    static Class getClass(String tableName) {
        Class tClass = null;
        try {
            tClass = Class.forName(tableName.replace("_", "."));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tClass;
    }

    public static boolean hasEmptyConstructor(Class tClass) {
        try {
            tClass.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    static int getClassVersion(Class tClass) {
        EntityVersion version = (EntityVersion) tClass.getAnnotation(EntityVersion.class);
        return version == null ? 0 : version.value();
    }

    static Column getFieldColumn(Field field) {
        EntityColumn column = field.getAnnotation(EntityColumn.class);
        if (column != null && column.hide()) {
            return null;
        }
        String type = columnType(field);
        if (type == null) {
            return null;
        }
        return new Column(field.getName(), type, column == null ? false : column.nullable(), column == null ? false : column.primary(), column == null ? false : column.autoIncrement());
    }

    static EntityColumn getColumn(Field field) {
        EntityColumn column = field.getAnnotation(EntityColumn.class);
        return column;
    }

    static boolean isColumn(Field field) {
        EntityColumn column = field.getAnnotation(EntityColumn.class);
        return column == null || !column.hide();
    }
}

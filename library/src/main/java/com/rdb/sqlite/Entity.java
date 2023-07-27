package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.annotation.EntityVersion;
import com.rdb.sqlite.converter.EntityColumnConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Entity {


    static Map<String, EntityColumnConverter> converters = new HashMap<>();

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

    public static EntityColumnConverter getEntityColumnConverter(String type) {
        return converters.get(type);
    }

    public static String getTableName(Class tClass) {
        return tClass.getName().replace(".", "_");
    }

    public static List<Column> getColumns(Class tClass) {
        List<Column> columns = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                Column column = getFieldColumn(fields[i]);
                if (column != null) {
                    columns.add(column);
                }
            }
        }
        Collections.sort(columns, new Comparator<Column>() {
            @Override
            public int compare(Column column1, Column column2) {
                return column1.getName().compareTo(column2.getName());
            }
        });
        return columns;
    }

    public static String[] getColumnNames(Class tClass) {
        List<String> columnNames = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                Column column = getFieldColumn(fields[i]);
                if (column != null) {
                    columnNames.add(column.getName());
                }
            }
        }
        Collections.sort(columnNames);
        return columnNames.toArray(new String[columnNames.size()]);
    }

    public static Column getFieldColumn(Field field) {
        EntityColumn column = field.getAnnotation(EntityColumn.class);
        if (column != null && column.hide()) {
            return null;
        }
        String type = columnType(field);
        if (type == null) {
            return null;
        }
        return new Column(field.getName(), type, column != null && column.nullable(), column != null && column.primary(), column != null && column.autoIncrement());
    }

    public static boolean isColumn(Field field) {
        EntityColumn column = field.getAnnotation(EntityColumn.class);
        if (column != null && column.hide()) {
            return false;
        }
        String type = columnType(field);
        if (type == null) {
            return false;
        }
        return true;
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

    public static String columnType(Field field) {
        EntityColumnConverter columnConverter = converters.get(field.getType().getName());
        return columnConverter == null ? null : columnConverter.getColumnType();
    }

    public static Class getClass(String tableName) {
        Class tClass = null;
        try {
            tClass = Class.forName(tableName.replace("_", "."));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tClass;
    }

    public static int getClassVersion(Class tClass) {
        EntityVersion version = (EntityVersion) tClass.getAnnotation(EntityVersion.class);
        return version == null ? 0 : version.value();
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

    public static String getCreateTableSQL(Class tClass) {
        List<Column> columns = getColumns(tClass);
        TableSQLBuilder builder = new TableSQLBuilder(getTableName(tClass));
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i) != null) {
                builder.addColumn(columns.get(i));
            }
        }
        return builder.build();
    }

    public static String getDropTableSQL(Class tClass) {
        return "DROP TABLE IF EXISTS " + getTableName(tClass);
    }
}

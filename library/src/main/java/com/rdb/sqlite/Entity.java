package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.annotation.EntityVersion;
import com.rdb.sqlite.converter.FeildConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Entity {

    static Map<String, FeildConverter> converters = new HashMap<>();

    static {
        converters.put(Boolean.TYPE.getName(), FeildConverter.BOOLEAN_CONVERTER);
        converters.put(Boolean.class.getName(), FeildConverter.BOOLEAN_CONVERTER);
        converters.put(byte[].class.getName(), FeildConverter.BYTES_CONVERTER);
        converters.put(Character.TYPE.getName(), FeildConverter.CHAR_CONVERTER);
        converters.put(Character.class.getName(), FeildConverter.CHAR_CONVERTER);
        converters.put(Byte.TYPE.getName(), FeildConverter.BYTE_CONVERTER);
        converters.put(Byte.class.getName(), FeildConverter.BYTE_CONVERTER);
        converters.put(Short.TYPE.getName(), FeildConverter.SHORT_CONVERTER);
        converters.put(Short.class.getName(), FeildConverter.SHORT_CONVERTER);
        converters.put(Integer.TYPE.getName(), FeildConverter.INTEGER_CONVERTER);
        converters.put(Integer.class.getName(), FeildConverter.INTEGER_CONVERTER);
        converters.put(Long.TYPE.getName(), FeildConverter.LONG_CONVERTER);
        converters.put(Long.class.getName(), FeildConverter.LONG_CONVERTER);
        converters.put(Float.TYPE.getName(), FeildConverter.FLOAT_CONVERTER);
        converters.put(Float.class.getName(), FeildConverter.FLOAT_CONVERTER);
        converters.put(Double.TYPE.getName(), FeildConverter.DOUBLE_CONVERTER);
        converters.put(Double.class.getName(), FeildConverter.DOUBLE_CONVERTER);
        converters.put(String.class.getName(), FeildConverter.STRING_CONVERTER);
    }

    static FeildConverter getFeildConverter(String type) {
        return converters.get(type);
    }

    static String getTableName(Class tClass) {
        return tClass.getName().replace(".", "_");
    }

    static void checkClass(Class tClass) {
        if (!haveEmptyConstructor(tClass)) {
            EntitySQLite.log(tClass + " does not have an empty constructor");
        }
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                EntityColumn column = field.getAnnotation(EntityColumn.class);
                if ((column == null || !column.hide()) && !converters.containsKey(field.getType().getName())) {
                    EntitySQLite.log(tClass + " unsupport field: " + field + " " + field.getType());
                }
            }
        }
    }

    static List<Column> getColumns(Class tClass) {
        List<Column> columns = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Column column = getFieldColumn(field);
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

    static String[] getColumnNames(Class tClass) {
        List<String> columnNames = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Column column = getFieldColumn(field);
                if (column != null) {
                    columnNames.add(column.getName());
                }
            }
        }
        Collections.sort(columnNames);
        return columnNames.toArray(new String[columnNames.size()]);
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
        return new Column(field.getName(), type, column != null && column.nullable(), column != null && column.primary(), column != null && column.autoIncrement());
    }

    static boolean isColumn(Field field) {
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

    static List<Field> getUnStaticDeclaredFields(Class tClass) {
        List<Field> fieldList = new ArrayList<>();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    static String columnType(Field field) {
        FeildConverter feildConverter = converters.get(field.getType().getName());
        return feildConverter == null ? null : feildConverter.getColumnType();
    }

    static Class getClass(String tableName) {
        Class tClass = null;
        try {
            tClass = Class.forName(tableName.replace("_", "."));
        } catch (ClassNotFoundException e) {
            EntitySQLite.log("getClass", e);
        }
        return tClass;
    }

    static int getClassVersion(Class tClass) {
        EntityVersion version = (EntityVersion) tClass.getAnnotation(EntityVersion.class);
        return version == null ? 0 : version.value();
    }

    static boolean haveEmptyConstructor(Class tClass) {
        try {
            tClass.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            EntitySQLite.log("hasEmptyConstructor", e);
        }
        return false;
    }

    static String getCreateTableSQL(Class tClass) {
        List<Column> columns = getColumns(tClass);
        TableSQLBuilder builder = new TableSQLBuilder(getTableName(tClass));
        for (Column column : columns) {
            if (column != null) {
                builder.addColumn(column);
            }
        }
        return builder.build();
    }

    static String getDropTableSQL(Class tClass) {
        return "DROP TABLE IF EXISTS " + getTableName(tClass);
    }
}

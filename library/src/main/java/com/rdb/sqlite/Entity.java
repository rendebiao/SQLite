package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Entity {

    private static final String TAG = Entity.class.getSimpleName();

    static String getTableName(Class tClass) {
        return tClass.getName().replace(".", "_");
    }

    static Class getClass(String tableName) {
        Class tClass = null;
        try {
            tClass = Class.forName(tableName.replace("_", "."));
        } catch (ClassNotFoundException e) {
            SQLite.e(TAG, "getClass", e);
        }
        return tClass;
    }

    static EntityTableInfo getEntityTableInfo(Class tClass) {
        EntityClass entityClass = (EntityClass) tClass.getAnnotation(EntityClass.class);
        if (entityClass == null) {
            return null;
        }
        EntityTableInfo tableInfo = new EntityTableInfo();
        tableInfo.setTableName(getTableName(tClass));
        tableInfo.setTableVersion(entityClass.version());
        tableInfo.setAutoCreateTable(entityClass.autoCreateTable());
        tableInfo.setColumns(getColumns(tClass, true));
        return tableInfo;
    }

    static <T> boolean checkClass(Class<T> tClass) {
        if (tClass == null) {
            SQLite.e(TAG, "tClass is null");
            return false;
        }

        if (!haveEmptyConstructor(tClass)) {
            SQLite.e(TAG, tClass + ": no empty constructor");
            return false;
        }

        EntityClass entityClass = tClass.getAnnotation(EntityClass.class);
        if (entityClass == null) {
            SQLite.e(TAG, tClass + ": no EntityClass");
            return false;
        }

        int count = 0;
        boolean hasPrimary = false;
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                Column column = getFieldColumn(field);
                if (column != null) {
                    count++;
                    hasPrimary |= column.isPrimary();
                }
            }
        }

        if (count == 0) {
            SQLite.e(TAG, tClass + ": no field");
            return false;
        } else if (!hasPrimary) {
            SQLite.w(TAG, tClass + ": no primary key");
        }
        return true;
    }

    static List<Column> getColumns(Class tClass, boolean sort) {
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
        if (sort) {
            Collections.sort(columns, new Comparator<Column>() {
                @Override
                public int compare(Column column1, Column column2) {
                    return column1.getName().compareTo(column2.getName());
                }
            });
        }
        return columns;
    }

    static Column getFieldColumn(Field field) {
        EntityColumn entityColumn = field.getAnnotation(EntityColumn.class);
        if (entityColumn != null && entityColumn.hidden()) {
            return null;
        }
        if (entityColumn == null) {
            return new Column(field, false, false, false);
        } else {
            return new Column(field, entityColumn.nullable(), entityColumn.primary(), entityColumn.autoIncrement());
        }
    }

    static int getClassVersion(Class tClass) {
        EntityClass entityClass = (EntityClass) tClass.getAnnotation(EntityClass.class);
        return entityClass == null ? 0 : entityClass.version();
    }

    static boolean haveEmptyConstructor(Class tClass) {
        try {
            tClass.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            SQLite.e(TAG, "hasEmptyConstructor", e);
        }
        return false;
    }

    static <T> T newObject(Class<T> tClass) {
        try {
            Constructor constructor = tClass.getConstructor();
            return (T) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            SQLite.e(TAG, "newObject", e);
        } catch (InvocationTargetException e) {
            SQLite.e(TAG, "newObject", e);
        } catch (IllegalAccessException e) {
            SQLite.e(TAG, "newObject", e);
        } catch (InstantiationException e) {
            SQLite.e(TAG, "newObject", e);
        }
        return null;
    }

    static String getCreateTableSQL(Class tClass, String tableName) {
        List<Column> columns = getColumns(tClass, true);
        return getCreateTableSQL(tableName, columns);
    }

    static String getCreateTableSQL(String tableName, List<Column> columns) {
        TableSQLBuilder builder = new TableSQLBuilder(tableName);
        for (Column column : columns) {
            builder.addColumn(column);
        }
        return builder.build();
    }

    static String getDropTableSQL(Class tClass) {
        return "DROP TABLE IF EXISTS " + getTableName(tClass);
    }
}
package com.rdb.sqlite;

import android.database.SQLException;

import com.rdb.sqlite.annotation.Entity;
import com.rdb.sqlite.annotation.EntityColumn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class EntityUtil {

    private static final String TAG = EntityUtil.class.getSimpleName();

    static String getTableName(Class entityClass) {
        return entityClass.getName().replace(".", "_");
    }

    static String getClassName(String tableName) {
        return tableName.replace("_", ".");
    }

    static Class getClass(String tableName) {
        Class cls = null;
        try {
            cls = Class.forName(getClassName(tableName));
        } catch (ClassNotFoundException e) {
            SQLite.e(TAG, "getClass", e);
        }
        return cls;
    }

    static TableInfo getEntityTableInfo(Class entityClass) {
        Entity entity = (Entity) entityClass.getAnnotation(Entity.class);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setName(getTableName(entityClass));
        tableInfo.setVersion(entity == null ? 0 : entity.version());
        tableInfo.setColumns(getColumns(entityClass, true));
        return tableInfo;
    }

    static void checkClass(Class entityClass, Class[] historyClasses) {
        if (entityClass == null) {
            throw new SQLException("class is null");
        }

        if (!haveEmptyConstructor(entityClass)) {
            throw new SQLException(entityClass + " has no empty constructor");
        }

        if (historyClasses != null) {
            for (Class cls : historyClasses) {
                if (!haveEmptyConstructor(cls)) {
                    throw new SQLException(cls + " has no empty constructor");
                }
            }
        }

        int count = 0;
        boolean hasPrimary = false;
        Field[] fields = entityClass.getDeclaredFields();
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
            throw new SQLException(entityClass + " has no available fields");
        } else if (!hasPrimary) {
            SQLite.w(TAG, entityClass + ": no primary key");
        }
    }

    static List<Column> getColumns(Class entityClass, boolean sort) {
        List<Column> columns = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();
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

    static int getClassVersion(Class entityClass) {
        Entity entity = (Entity) entityClass.getAnnotation(Entity.class);
        return entity == null ? 0 : entity.version();
    }

    static boolean haveEmptyConstructor(Class entityClass) {
        try {
            entityClass.getConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            SQLite.e(TAG, "hasEmptyConstructor", e);
        }
        return false;
    }

    static boolean isImplementsInterface(Class cls, Class inteface) {
        if (cls != null) {
            Class[] interfaces = cls.getInterfaces();
            for (Class in : interfaces) {
                if (in == inteface) {
                    return true;
                }
            }
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

    static String getCreateTableSQL(Class entityClass, String tableName) {
        List<Column> columns = getColumns(entityClass, true);
        return getCreateTableSQL(tableName, columns);
    }

    static String getCreateTableSQL(String tableName, List<Column> columns) {
        TableSQLBuilder builder = new TableSQLBuilder(tableName);
        for (Column column : columns) {
            builder.addColumn(column);
        }
        return builder.build();
    }

    static String getDropTableSQL(Class entityClass) {
        return "DROP TABLE IF EXISTS " + getTableName(entityClass);
    }
}

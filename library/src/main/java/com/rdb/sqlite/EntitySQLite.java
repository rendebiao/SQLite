package com.rdb.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.rdb.sqlite.converter.FeildConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySQLite {

    private final SQLiteHelper helper;
    private final EntityTableListener entityTableListener;
    private final Map<Class, EntityTable> tableMap = new HashMap<>();
    private final Map<Class, EntityTableInfo> tableInfoMap = new HashMap<>();
    private EntityTable<EntityTableInfo> tableInfoTable;

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        this(context, name, factory, null, null);
    }

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener) {
        this(context, name, factory, entityTableListener, null);
    }

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener, DatabaseErrorHandler errorHandler) {
        helper = new EntitySQLiteHelper(context, name, factory, 1, errorHandler);
        this.entityTableListener = entityTableListener;
    }

    static void log(String msg) {
        Log.e("EntitySQLite", msg);
    }

    static void log(String msg, Exception e) {
        Log.e("EntitySQLite", msg, e);
    }

    public static void registerFeildConverter(String type, FeildConverter feildConverter) {
        Entity.converters.put(type, feildConverter);
    }

    public static void checkEntityClass(Class tClass) {
        Entity.checkClass(tClass);
    }

    private synchronized void initTableInfoTable() {
        if (tableInfoTable == null) {
            createTable(EntityTableInfo.class);
            tableInfoTable = table(EntityTableInfo.class);
            List<EntityTableInfo> tableInfoList = tableInfoTable.queryList("1 = 1", new String[]{}, null, null, null);
            for (EntityTableInfo tableInfo : tableInfoList) {
                Class tClass = Entity.getClass(tableInfo.getTableName());
                if (tClass != null) {
                    if (Entity.haveEmptyConstructor(tClass)) {
                        tableInfoMap.put(tClass, tableInfo);
                        int classVersion = Entity.getClassVersion(tClass);
                        String createTableSql = Entity.getCreateTableSQL(tClass);
                        boolean updateInfo = false;
                        boolean recreateTable = false;
                        if (classVersion != tableInfo.getTableVersion()) {
                            log(tClass.getSimpleName() + " version changed " + tableInfo.getTableVersion() + " to " + classVersion);
                            updateInfo = true;
                            recreateTable = entityTableListener == null || !entityTableListener.onVersionChanged(helper, tClass, tableInfo.getTableName(), tableInfo.getTableVersion(), classVersion);
                        } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                            log(tClass.getSimpleName() + " table changed " + tableInfo.getTableSqlString() + " to " + createTableSql);
                            updateInfo = true;
                            recreateTable = true;
                        }
                        if (recreateTable) {
                            if (entityTableListener != null) {
                                entityTableListener.beforeRecreteTable(helper, tClass, tableInfo.getTableName());
                            }
                            SQLiteOperator.execSQL(helper, Entity.getDropTableSQL(tClass));
                            SQLiteOperator.execSQL(helper, createTableSql);
                            if (entityTableListener != null) {
                                entityTableListener.afterRecreteTable(helper, tClass, tableInfo.getTableName());
                            }
                        }
                        if (updateInfo) {
                            tableInfo.setTableSqlString(createTableSql);
                            tableInfo.setTableVersion(classVersion);
                            tableInfoTable.replace(tableInfo);
                        }
                    } else {
                        throw new RuntimeException(tClass + " has not empty constructor");
                    }
                }
            }
        }
    }

    public <T> EntityTable<T> table(Class<T> tClass) {
        EntityTable<T> table = tableMap.get(tClass);
        if (table == null) {
            createTable(tClass);
            table = new EntityTable(helper, tClass);
            tableMap.put(tClass, table);
        }
        return table;
    }

    private synchronized void createTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
            EntityTableInfo tableInfo = tableInfoMap.get(tClass);
            if (tableInfo == null) {
                String tableName = Entity.getTableName(tClass);
                tableInfo = new EntityTableInfo(tableName, 0, Entity.getCreateTableSQL(tClass));
                tableInfoTable.replace(tableInfo);
                tableInfoMap.put(tClass, tableInfo);
                SQLiteOperator.execSQL(helper, "DROP TABLE IF EXISTS " + tableName);
            }
            SQLiteOperator.execSQL(helper, tableInfo.getTableSqlString());
        }
    }

    public synchronized void dropTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
            String tableName = Entity.getTableName(tClass);
            tableInfoTable.delete("tableName = ?", new String[]{tableName});
            tableInfoMap.remove(tClass);
            SQLiteOperator.dropTable(helper, tableName);
        }
    }

    public int getTableVersion(Class tClass) {
        initTableInfoTable();
        if (tableInfoMap.containsKey(tClass)) {
            return tableInfoMap.get(tClass).getTableVersion();
        }
        return -1;
    }

    class EntitySQLiteHelper extends SQLiteHelper {

        public EntitySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                  int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Entity.getCreateTableSQL(EntityTableInfo.class));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

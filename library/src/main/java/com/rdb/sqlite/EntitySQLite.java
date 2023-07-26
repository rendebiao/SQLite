package com.rdb.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySQLite {

    private SQLiteHelper helper;
    private EntityTableListener entityTableListener;
    private EntityTable<EntityTableInfo> tableInfoTable;
    private Map<Class, EntityTable> tableMap = new HashMap<>();
    private Map<Class, EntityTableInfo> tableInfoMap = new HashMap<>();

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        this(context, name, factory, null, null);
    }

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener) {
        this(context, name, factory, entityTableListener, null);
    }

    public EntitySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener, DatabaseErrorHandler errorHandler) {
        helper = new SQLiteHelper(context, name, factory, 1, errorHandler) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(EntityUtils.getCreateTableSQL(EntityTableInfo.class));
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        this.entityTableListener = entityTableListener;
    }

    private void initTableInfoTable() {
        if (tableInfoTable == null) {
            createTable(EntityTableInfo.class);
            tableInfoTable = new EntityTable<>(helper, EntityTableInfo.class);
            List<EntityTableInfo> tableInfoList = tableInfoTable.queryList(null, "1 = 1", new String[]{}, null, null, null);
            for (EntityTableInfo tableInfo : tableInfoList) {
                Class tClass = EntityUtils.getClass(tableInfo.getTableName());
                if (tClass != null) {
                    if (EntityUtils.hasEmptyConstructor(tClass)) {
                        tableInfoMap.put(tClass, tableInfo);
                        int classVersion = EntityUtils.getClassVersion(tClass);
                        String createTableSql = EntityUtils.getCreateTableSQL(tClass);
                        boolean updateInfo = false;
                        boolean recreateTable = false;
                        if (classVersion != tableInfo.getTableVersion()) {
                            EntityUtils.log(tClass.getSimpleName() + " version changed " + tableInfo.getTableVersion() + " to " + classVersion);
                            updateInfo = true;
                            recreateTable = entityTableListener == null || !entityTableListener.onVersionChanged(helper, tClass, tableInfo.getTableName(), tableInfo.getTableVersion(), classVersion);
                        } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                            EntityUtils.log(tClass.getSimpleName() + " table changed " + tableInfo.getTableSqlString() + " to " + createTableSql);
                            updateInfo = true;
                            recreateTable = true;
                        }
                        if (recreateTable) {
                            if (entityTableListener != null) {
                                entityTableListener.beforeRecreteTable(helper, tClass, tableInfo.getTableName());
                            }
                            SQLiteOperator.execSQL(helper, EntityUtils.getDropTableSQL(tClass));
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

    public int getTableVersion(Class tClass) {
        initTableInfoTable();
        if (tableInfoMap.containsKey(tClass)) {
            return tableInfoMap.get(tClass).getTableVersion();
        }
        return -1;
    }

    public <T> EntityTable<T> table(Class<T> tClass) {
        EntityTable<T> table = tableMap.get(tClass);
        if (table == null) {
            table = new EntityTable(helper, tClass);
            tableMap.put(tClass, table);
        }
        return table;
    }

    synchronized void createTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
            EntityTableInfo tableInfo = tableInfoMap.get(tClass);
            if (tableInfo == null) {
                String tableName = EntityUtils.getTableName(tClass);
                tableInfo = new EntityTableInfo(tableName, 0, EntityUtils.getCreateTableSQL(tClass));
                tableInfoTable.replace(tableInfo);
                tableInfoMap.put(tClass, tableInfo);
                SQLiteOperator.execSQL(helper, "DROP TABLE IF EXISTS " + tableName);
            }
            SQLiteOperator.execSQL(helper, tableInfo.getTableSqlString());
            helper.closeDatabase();
        }
    }

    synchronized void dropTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
            String tableName = EntityUtils.getTableName(tClass);
            tableInfoTable.delete("tableName = ?", new String[]{tableName});
            tableInfoMap.remove(tClass);
            SQLiteOperator.dropTable(helper, tableName);
        }
    }
}

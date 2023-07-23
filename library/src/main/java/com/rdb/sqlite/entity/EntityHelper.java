package com.rdb.sqlite.entity;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.rdb.sqlite.SQLiteHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHelper extends SQLiteHelper {

    private EntityTableListener entityTableListener;
    private EntityTable<EntityTableInfo> tableInfoTable;
    private Map<Class, EntityTable> tableMap = new HashMap<>();
    private Map<Class, EntityTableInfo> tableInfoMap = new HashMap<>();

    public EntityHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        this(context, name, factory, null, null);
    }

    public EntityHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener) {
        this(context, name, factory, entityTableListener, null);
    }

    public EntityHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, EntityTableListener entityTableListener, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, 1, errorHandler);
        this.entityTableListener = entityTableListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Entity.getCreateTableSQL(EntityTableInfo.class));
    }

    private void init() {
        if (tableInfoTable == null) {
            tableInfoTable = new EntityTable<>(this, EntityTableInfo.class);
            List<EntityTableInfo> tableInfoList = tableInfoTable.queryList(null, "1 = 1", new String[]{}, null, null, null);
            for (EntityTableInfo tableInfo : tableInfoList) {
                Class tClass = Entity.getClass(tableInfo.getTableName());
                if (tClass != null) {
                    if (Entity.hasEmptyConstructor(tClass)) {
                        tableInfoMap.put(tClass, tableInfo);
                        int classVersion = Entity.getClassVersion(tClass);
                        String createTableSql = Entity.getCreateTableSQL(tClass);
                        boolean updateInfo = false;
                        boolean recreateTable = false;
                        if (classVersion != tableInfo.getTableVersion()) {
                            Entity.log(tClass.getSimpleName() + " version changed " + tableInfo.getTableVersion() + " to " + classVersion);
                            updateInfo = true;
                            recreateTable = entityTableListener == null || !entityTableListener.onVersionChanged(this, tClass, tableInfo.getTableName(), tableInfo.getTableVersion(), classVersion);
                        } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                            Entity.log(tClass.getSimpleName() + " table changed " + tableInfo.getTableSqlString() + " to " + createTableSql);
                            updateInfo = true;
                            recreateTable = true;
                        }
                        if (recreateTable) {
                            if (entityTableListener != null) {
                                entityTableListener.beforeRecreteTable(this, tClass, tableInfo.getTableName());
                            }
                            SQLiteDatabase sqLiteDatabase = openDatabase();
                            try {
                                sqLiteDatabase.execSQL(Entity.getDropTableSQL(tClass));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                sqLiteDatabase.execSQL(createTableSql);
                            }
                            closeDatabase();
                            if (entityTableListener != null) {
                                entityTableListener.afterRecreteTable(this, tClass, tableInfo.getTableName());
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

    @Override
    public final void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getTableVersion(Class tClass) {
        init();
        if (tableInfoMap.containsKey(tClass)) {
            return tableInfoMap.get(tClass).getTableVersion();
        }
        return -1;
    }


    public <T> EntityTable<T> table(Class<T> tClass) {
        EntityTable<T> table = tableMap.get(tClass);
        if (table == null) {
            table = new EntityTable(this, tClass);
            tableMap.put(tClass, table);
        }
        return table;
    }

    synchronized void addTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            init();
            EntityTableInfo tableInfo = tableInfoMap.get(tClass);
            SQLiteDatabase sqLiteDatabase = openDatabase();
            if (tableInfo == null) {
                String tableName = Entity.getTableName(tClass);
                tableInfo = new EntityTableInfo(tableName, 0, Entity.getCreateTableSQL(tClass));
                tableInfoTable.replace(tableInfo);
                tableInfoMap.put(tClass, tableInfo);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
            sqLiteDatabase.execSQL(tableInfo.getTableSqlString());
            closeDatabase();
        }
    }

    synchronized void delTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            init();
            String tableName = Entity.getTableName(tClass);
            tableInfoTable.delete("tableName = ?", new String[]{tableName});
            tableInfoMap.remove(tClass);
        }
    }
}

package com.rdb.sqlite;

import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLite {

    private static final boolean debug = true;
    private final SQLiteOpener sqLiteOpener;
    private final SQLiteOperator sqLiteOperator;
    private final Map<String, Table> tableMap = new HashMap<>();
    private final Map<Class, TableInfo> tableInfoMap = new HashMap<>();
    private Table tableInfoTable;
    private HistoryEntity historyEntity;

    public SQLite(SQLiteOpenHelper openHelper) {
        this.sqLiteOpener = new SQLiteOpener(openHelper);
        this.sqLiteOperator = new SQLiteOperator(sqLiteOpener);
    }

    static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag(tag), msg);
        }
    }

    static void w(String tag, String msg) {
        if (debug) {
            Log.w(tag(tag), msg);
        }
    }

    static void e(String tag, String msg) {
        Log.e(tag(tag), msg);
    }

    static void e(String tag, String msg, Exception e) {
        Log.e(tag(tag), msg, e);
    }

    private static String tag(String tag) {
        return TextUtils.isEmpty(tag) ? "SQLite" : ("SQLite-" + tag);
    }

    public void init(HistoryEntity historyEntity, JsonConverter jsonConverter) {
        this.historyEntity = historyEntity;
        this.sqLiteOperator.setConverter(new EntityConverter(jsonConverter));
        initTableInfoTable();
    }

    public void checkClass(Class entityClass) {
        EntityUtil.checkClass(entityClass, historyEntity.getHistoryClasses(entityClass));
    }

    public boolean createTable(String sql) {
        return sqLiteOperator.execSQL(sql);
    }

    public boolean createTable(TableSQLBuilder builder) {
        return sqLiteOperator.execSQL(builder.build());
    }

    public Table table(String tableName) {
        Table table = tableMap.get(tableName);
        if (table == null) {
            table = new Table(sqLiteOperator, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }

    public boolean dropTable(String tableName) {
        return sqLiteOperator.dropTable(tableName);
    }

    public Table table(Class entityClass) {
        try {
            checkClass(entityClass);
        } catch (Exception e) {
            e(null, "checClass fail", e);
            return null;
        }
        String tableName = EntityUtil.getTableName(entityClass);
        Table table = tableMap.get(entityClass);
        if (table == null) {
            TableInfo tableInfo = tableInfoMap.get(entityClass);
            if (tableInfo == null) {
                tableInfo = EntityUtil.getEntityTableInfo(entityClass);
                if (tableInfo == null) {
                    d(null, "table fail, tableInfo = null");
                    return null;
                }
                sqLiteOperator.execSQL(tableInfo.getTableSqlString());
                if (!entityClass.equals(TableInfo.class)) {
                    tableInfoTable.replace(tableInfo);
                }
                tableInfoMap.put(entityClass, tableInfo);
            }
            table = new Table(sqLiteOperator, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }

    public synchronized void dropTable(Class entityClass) {
        if (entityClass != null && !entityClass.equals(TableInfo.class)) {
            String tableName = EntityUtil.getTableName(entityClass);
            tableInfoTable.delete("name = ?", new String[]{tableName});
            tableInfoMap.remove(entityClass);
            sqLiteOperator.dropTable(tableName);
        }
    }

    public int getTableVersion(Class entityClass) {
        if (tableInfoMap.containsKey(entityClass)) {
            return tableInfoMap.get(entityClass).getVersion();
        }
        return -1;
    }

    public boolean isTableExists(String tableName) {
        return sqLiteOperator.isTableExists(tableName);
    }

    private synchronized void initTableInfoTable() {
        if (tableInfoTable == null) {
            SQLite.d(null, "initTableInfoTable");
            tableInfoTable = table(TableInfo.class);
            List<TableInfo> tableInfoList = tableInfoTable.queryList(TableInfo.class, "1 = 1", new String[]{}, null, null, null);
            for (TableInfo tableInfo : tableInfoList) {
                Class entityClass = EntityUtil.getClass(tableInfo.getName());
                if (entityClass == null) {
                    String className = EntityUtil.getClassName(tableInfo.getName());
                    d(null, "unfound class " + className);
                    continue;
                }
                checkClass(entityClass);
                int classVersion = EntityUtil.getClassVersion(entityClass);
                String createTableSql = EntityUtil.getCreateTableSQL(entityClass, tableInfo.getName());
                if (classVersion != tableInfo.getVersion()) {
                    Class historyClass = historyEntity.getHistoryClass(entityClass, tableInfo.getVersion());
                    boolean supportHistory = EntityUtil.isImplementsInterface(historyClass, HistoryConverter.class);
                    d(null, entityClass.getName() + " version: " + tableInfo.getVersion() + " to " + classVersion + ", supportHistory = " + supportHistory + ", historyClass = " + historyClass);
                    if (supportHistory) {
                        d(null, historyClass + " convert to " + entityClass);
                        String newTableName = EntityUtil.getTableName(historyClass);
                        sqLiteOperator.alterTable(tableInfo.getName(), newTableName);
                        tableInfoMap.remove(entityClass);
                        Table table = table(entityClass);
                        Table alterTable = table(historyClass);
                        List<HistoryConverter> list = alterTable.queryAll(historyClass);
                        for (HistoryConverter converter : list) {
                            Object entity = converter.toCurrent();
                            d(null, converter + " convert to " + entity);
                            if (entity != null && entity.getClass() == entityClass) {
                                table.insert(entity);
                            }
                        }
                    }
                    dropTable(historyClass);
                    boolean delete = tableInfoTable.delete("? == ?", new String[]{"tableName", tableInfo.getName()});
                    d(null, "table " + tableInfo.getName() + " is delete");
                    return;
                } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                    throw new SQLException("class " + entityClass.getName() + " is changed, but version is not changed");
                }
                tableInfoMap.put(entityClass, tableInfo);
            }
        }
    }

    public boolean execSQL(String sql) {
        return sqLiteOperator.execSQL(sql);
    }

    public boolean execSQL(String sql, Object[] bindArgs) {
        return sqLiteOperator.execSQL(sql, bindArgs);
    }
}

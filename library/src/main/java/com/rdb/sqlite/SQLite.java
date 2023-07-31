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
    private SQLiteLinstener sqLiteLinstener;
    private Table tableInfoTable;

    public SQLite(SQLiteOpenHelper openHelper) {
        this.sqLiteOpener = new SQLiteOpener(openHelper);
        this.sqLiteOperator = new SQLiteOperator(sqLiteOpener);
    }

    public static <T> void checkClass(Class<T> tClass) {
        Entity.checkClass(tClass);
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

    public void init(JsonConverter jsonConverter, SQLiteLinstener sqLiteLinstener) {
        this.sqLiteLinstener = sqLiteLinstener;
        this.sqLiteOperator.setConverter(new EntityConverter(jsonConverter));
        initTableInfoTable();
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

    public <T> Table table(Class<T> tClass) {
        try {
            Entity.checkClass(tClass);
        } catch (Exception e) {
            e(null, "checClass fail", e);
        }
        String tableName = Entity.getTableName(tClass);
        Table table = tableMap.get(tClass);
        if (table == null) {
            TableInfo tableInfo = tableInfoMap.get(tClass);
            if (tableInfo == null) {
                tableInfo = Entity.getEntityTableInfo(tClass);
                if (tableInfo == null) {
                    d(null, "table fail, tableInfo = null");
                    return null;
                }
                if (!tClass.equals(TableInfo.class)) {
                    tableInfoTable.replace(tableInfo);
                }
                tableInfoMap.put(tClass, tableInfo);
                sqLiteOperator.execSQL(tableInfo.getTableSqlString());
            }
            table = new Table(sqLiteOperator, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }

    public synchronized void dropTable(Class tClass) {
        if (tClass != null && !tClass.equals(TableInfo.class)) {
            String tableName = Entity.getTableName(tClass);
            tableInfoTable.delete("tableName = ?", new String[]{tableName});
            tableInfoMap.remove(tClass);
            sqLiteOperator.dropTable(tableName);
        }
    }

    public int getTableVersion(Class tClass) {
        if (tableInfoMap.containsKey(tClass)) {
            return tableInfoMap.get(tClass).getVersion();
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
                Class tClass = Entity.getClass(tableInfo.getName());
                if (tClass == null) {
                    String className = Entity.getClassName(tableInfo.getName());
                    d(null, "unfound class " + className);
                    continue;
                }
                Entity.checkClass(tClass);
                int classVersion = Entity.getClassVersion(tClass);
                String createTableSql = Entity.getCreateTableSQL(tClass, tableInfo.getName());
                boolean dropTable = false;
                if (classVersion != tableInfo.getVersion()) {
                    d(null, "class " + tClass.getName() + " version changed: " + tableInfo.getVersion() + " to " + classVersion);
                    dropTable = true;
                } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                    throw new SQLException("class " + tClass.getName() + " is changed, but version is not changed");
                }
                if (dropTable) {
                    if (sqLiteLinstener == null) {
                        dropTable(tableInfo.getName());
                    } else {
                        String newTableName = tableInfo.getName() + "_alter";
                        sqLiteOperator.alterTable(tableInfo.getName(), newTableName);
                        Table alterTable = new Table(sqLiteOperator, newTableName);
                        sqLiteLinstener.onTableVersionChanged(tClass, tableInfo.getVersion(), alterTable);
                        dropTable(newTableName);
                    }
                    boolean delete = tableInfoTable.delete("? == ?", new String[]{"tableName", tableInfo.getName()});
                    d(null, "table " + tableInfo.getName() + " is delete");
                } else {
                    tableInfoMap.put(tClass, tableInfo);
                }
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

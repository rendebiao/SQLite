package com.rdb.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLite {

    private static final boolean debug = true;
    private final SQLiteHelper helper;
    private final SQLiteOperator sqLiteOperator;
    private final SQLiteLinstener sqLiteLinstener;
    private final Map<String, Table> tableMap = new HashMap<>();
    private final Map<Class, EntityTableInfo> tableInfoMap = new HashMap<>();
    private Table tableInfoTable;

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler, SQLiteLinstener sqLiteLinstener, @NonNull JsonConverter jsonConverter) {
        this.helper = new SQLiteHelper(context, name, factory, version, errorHandler, sqLiteLinstener);
        this.sqLiteOperator = new SQLiteOperator(helper, new EntityConverter(jsonConverter));
        this.sqLiteLinstener = sqLiteLinstener;
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
        if (tClass == null) {
            d(null, "table fail, tClass = null");
            return null;
        }
        if (!tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
        }
        String tableName = Entity.getTableName(tClass);
        Table table = tableMap.get(tClass);
        if (table == null) {
            EntityTableInfo tableInfo = tableInfoMap.get(tClass);
            if (tableInfo == null) {
                tableInfo = Entity.getEntityTableInfo(tClass);
                if (tableInfo == null) {
                    d(null, "table fail, tableInfo = null");
                    return null;
                }
                if (!tClass.equals(EntityTableInfo.class)) {
                    tableInfoTable.replace(tableInfo);
                }
                tableInfoMap.put(tClass, tableInfo);
                if (tableInfo.isAutoCreateTable()) {
                    sqLiteOperator.execSQL(tableInfo.getTableSqlString());
                }
            }
            table = new Table(sqLiteOperator, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }

    public synchronized void dropTable(Class tClass) {
        if (tClass != null && !tClass.equals(EntityTableInfo.class)) {
            initTableInfoTable();
            String tableName = Entity.getTableName(tClass);
            tableInfoTable.delete("tableName = ?", new String[]{tableName});
            tableInfoMap.remove(tClass);
            sqLiteOperator.dropTable(tableName);
        }
    }

    public int getTableVersion(Class tClass) {
        initTableInfoTable();
        if (tableInfoMap.containsKey(tClass)) {
            return tableInfoMap.get(tClass).getTableVersion();
        }
        return -1;
    }

    public boolean isTableExists(String tableName) {
        return sqLiteOperator.isTableExists(tableName);
    }

    private synchronized void initTableInfoTable() {
        if (tableInfoTable == null) {
            SQLite.d(null, "initTableInfoTable");
            tableInfoTable = table(EntityTableInfo.class);
            List<EntityTableInfo> tableInfoList = tableInfoTable.queryList(EntityTableInfo.class, "1 = 1", new String[]{}, null, null, null);
            for (EntityTableInfo tableInfo : tableInfoList) {
                Class tClass = Entity.getClass(tableInfo.getTableName());
                if (tClass == null) {
                    continue;
                }
                if (Entity.checkClass(tClass)) {
                    int classVersion = Entity.getClassVersion(tClass);
                    String createTableSql = Entity.getCreateTableSQL(tClass, tableInfo.getTableName());
                    boolean dropTable = false;
                    if (classVersion != tableInfo.getTableVersion()) {
                        d(null, tClass.getSimpleName() + " version changed " + tableInfo.getTableVersion() + " to " + classVersion);
                        dropTable = true;
                    } else if (!TextUtils.equals(createTableSql, tableInfo.getTableSqlString())) {
                        d(null, tClass.getSimpleName() + " table changed " + tableInfo.getTableSqlString() + " to " + createTableSql);
                        dropTable = true;
                    }
                    if (dropTable) {
                        if (sqLiteLinstener == null) {
                            dropTable(tableInfo.getTableName());
                        } else {
                            String newTableName = tableInfo.getTableName() + "_alter";
                            sqLiteOperator.alterTable(tableInfo.getTableName(), newTableName);
                            Table alterTable = new Table(sqLiteOperator, newTableName);
                            sqLiteLinstener.onTableAlterByUpgrade(tClass, alterTable);
                            dropTable(newTableName);
                        }
                        boolean delete = tableInfoTable.delete("? == ?", new String[]{"tableName", tableInfo.getTableName()});
                        d(null, "table " + tableInfo.getTableName() + " is delete");
                    } else {
                        tableInfoMap.put(tClass, tableInfo);
                    }
                } else {
                    throw new RuntimeException(tClass + " checked fail");
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

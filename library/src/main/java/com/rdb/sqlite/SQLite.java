package com.rdb.sqlite;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SQLite {

    private final SQLiteHelper helper;
    private final Map<String, Table> tableMap = new HashMap<>();

    public SQLite(SQLiteHelper helper) {
        this.helper = helper;
    }

    static void log(String msg) {
        Log.e("SQLite", msg);
    }

    public Table table(String tableName) {
        Table table = tableMap.get(tableName);
        if (table == null) {
            table = new Table(helper, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }

    public boolean createTable(String sql) {
        return SQLiteOperator.execSQL(helper, sql);
    }

    public boolean createTable(TableSQLBuilder builder) {
        return SQLiteOperator.execSQL(helper, builder.build());
    }

    public boolean dropTable(String tableName) {
        return SQLiteOperator.dropTable(helper, tableName);
    }

    public boolean execSQL(String sql) {
        return SQLiteOperator.execSQL(helper, sql);
    }

    public boolean execSQL(String sql, Object[] bindArgs) {
        return SQLiteOperator.execSQL(helper, sql, bindArgs);
    }
}

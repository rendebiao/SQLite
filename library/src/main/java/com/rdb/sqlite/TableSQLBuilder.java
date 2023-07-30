package com.rdb.sqlite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableSQLBuilder {

    private final String tableName;
    private final StringBuffer sql = new StringBuffer();
    private final List<Column> columns = new ArrayList<>();
    private final List<Column> primaryColumns = new ArrayList<>();

    public TableSQLBuilder(String tableName) {
        this.tableName = tableName;
    }

    public TableSQLBuilder addColumn(Column column) {
        if (column.isPrimary()) {
            primaryColumns.add(column);
        } else {
            columns.add(column);
        }
        return this;
    }

    public TableSQLBuilder addPrimaryColumn(String name, DataType type, boolean autoIncrement) {
        primaryColumns.add(new Column(name, type, false, true, autoIncrement));
        return this;
    }

    public TableSQLBuilder addColumn(String name, DataType type, boolean nullable) {
        columns.add(new Column(name, type, nullable, false, false));
        return this;
    }

    public String build() {
        Collections.sort(columns);
        Collections.sort(primaryColumns);
        sql.append("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        columns.addAll(0, primaryColumns);
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(column.getName() + " " + column.getType());
            if (column.isPrimary()) {
                if (primaryColumns.size() == 1) {
                    sql.append(" PRIMARY KEY");
                }
                if (column.isAutoIncrement()) {
                    sql.append(" AUTOINCREMENT");
                }
            }
            if (!column.isNullable()) {
                sql.append(" NOT NULL");
            }
        }
        if (primaryColumns.size() >= 2) {
            sql.append(", PRIMARY KEY (");
            for (int i = 0; i < primaryColumns.size(); i++) {
                Column column = primaryColumns.get(i);
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(column.getName());
            }
            sql.append("))");
        } else {
            sql.append(")");
        }
        return sql.toString();
    }
}

package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

import java.util.List;

@EntityClass(version = 0, autoCreateTable = true)
class EntityTableInfo {

    @EntityColumn(primary = true)
    private String tableName;

    private int tableVersion;

    private boolean autoCreateTable;

    private List<Column> columns;

    public EntityTableInfo() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTableVersion() {
        return tableVersion;
    }

    public void setTableVersion(int tableVersion) {
        this.tableVersion = tableVersion;
    }

    public String getTableSqlString() {
        return Entity.getCreateTableSQL(tableName, columns);
    }

    public boolean isAutoCreateTable() {
        return autoCreateTable;
    }

    public void setAutoCreateTable(boolean autoCreateTable) {
        this.autoCreateTable = autoCreateTable;
    }

    public List<Column> getColumns() {
        return columns;
    }

    void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    Column getPrimaryColumn() {
        for (Column column : columns) {
            if (column.isPrimary()) {
                return column;
            }
        }
        return null;
    }
}

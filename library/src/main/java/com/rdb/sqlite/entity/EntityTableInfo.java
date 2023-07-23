package com.rdb.sqlite.entity;

import com.rdb.sqlite.entity.annotation.EntityColumn;
import com.rdb.sqlite.entity.annotation.EntityVersion;

@EntityVersion(value = 0)
class EntityTableInfo {

    @EntityColumn(hide = false, primary = true, nullable = false)
    private String tableName;
    @EntityColumn(hide = false, primary = false, nullable = false)
    private int tableVersion;
    @EntityColumn(hide = false, primary = false, nullable = false)
    private String tableSqlString;

    public EntityTableInfo() {

    }

    public EntityTableInfo(String tableName, int tableVersion, String tableSqlString) {
        this.tableName = tableName;
        this.tableVersion = tableVersion;
        this.tableSqlString = tableSqlString;
    }

    public String getTableName() {
        return tableName;
    }

    public int getTableVersion() {
        return tableVersion;
    }

    void setTableVersion(int tableVersion) {
        this.tableVersion = tableVersion;
    }

    public String getTableSqlString() {
        return tableSqlString;
    }

    void setTableSqlString(String tableSqlString) {
        this.tableSqlString = tableSqlString;
    }
}

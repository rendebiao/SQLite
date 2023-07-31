package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

import java.util.List;

@EntityClass(version = 0)
class TableInfo {

    @EntityColumn(primary = true)
    private String name;

    private int version;

    private List<Column> columns;

    public TableInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTableSqlString() {
        return Entity.getCreateTableSQL(name, columns);
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

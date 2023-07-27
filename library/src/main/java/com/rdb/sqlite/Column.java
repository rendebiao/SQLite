package com.rdb.sqlite;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

public class Column implements Comparable<Column> {

    public static final String REAL = "REAL";
    public static final String TEXT = "TEXT";
    public static final String BLOB = "BLOB";
    public static final String INTEGER = "INTEGER";
    private final String name;
    private final String type;
    private final boolean nullable;
    private final boolean primary;
    private final boolean autoIncrement;

    public Column(String name, String type, boolean nullable, boolean primary, boolean autoIncrement) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.primary = primary;
        this.autoIncrement = autoIncrement;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPrimary() {
        return primary;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    @Override
    public int compareTo(@NonNull Column o) {
        return name.compareTo(o.name);
    }

    @StringDef({REAL, TEXT, BLOB, INTEGER})
    public @interface Type {
    }
}

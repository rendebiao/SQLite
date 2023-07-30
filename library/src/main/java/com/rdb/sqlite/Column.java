package com.rdb.sqlite;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

class Column implements Comparable<Column> {

    private final String name;
    private final String type;
    private final boolean nullable;
    private final boolean primary;
    private final boolean autoIncrement;

    public Column(Field field, boolean nullable, boolean primary, boolean autoIncrement) {
        this(field.getName(), EntityConverter.getDataType(field), nullable, primary, autoIncrement);
    }

    public Column(String name, DataType type, boolean nullable, boolean primary, boolean autoIncrement) {
        this.name = name;
        this.type = type.type;
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
}

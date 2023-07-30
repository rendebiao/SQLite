package com.rdb.sqlite;

public enum DataType {

    TEXT("TEXT"),
    REAL("REAL"),
    BLOB("BLOB"),
    INTEGER("INTEGER");

    String type;

    DataType(String type) {
        this.type = type;
    }
}

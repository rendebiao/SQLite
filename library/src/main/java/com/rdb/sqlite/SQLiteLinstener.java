package com.rdb.sqlite;

public interface SQLiteLinstener {

    void onTableVersionChanged(Class tClass, int oldVersion, Table alterTable);
}

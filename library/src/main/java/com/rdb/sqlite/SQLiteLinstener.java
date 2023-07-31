package com.rdb.sqlite;

public interface SQLiteLinstener {

    void onTableAlteredByClassChanged(Class tClass, Table alterTable);
}

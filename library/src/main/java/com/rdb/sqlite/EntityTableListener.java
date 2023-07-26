package com.rdb.sqlite;

public interface EntityTableListener {

    /**
     * 返回false 将删除表数据重新建表
     *
     * @param entityHelper
     * @param tClass
     * @param tableName
     * @param oldVersion
     * @param newVersion
     * @return
     */
    boolean onVersionChanged(SQLiteHelper entityHelper, Class tClass, String tableName, int oldVersion, int newVersion);

    void beforeRecreteTable(SQLiteHelper entityHelper, Class tClass, String tableName);

    void afterRecreteTable(SQLiteHelper entityHelper, Class tClass, String tableName);
}

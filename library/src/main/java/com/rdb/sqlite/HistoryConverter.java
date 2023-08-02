package com.rdb.sqlite;

public interface HistoryConverter<T> {

    T toCurrent();
}

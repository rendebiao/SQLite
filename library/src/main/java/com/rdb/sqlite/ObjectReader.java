package com.rdb.sqlite;

public interface ObjectReader<T> {

    T readColumn(int position, ValuesGetter valuesGetter);
}

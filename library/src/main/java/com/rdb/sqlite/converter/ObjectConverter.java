package com.rdb.sqlite.converter;

import android.content.ContentValues;

public interface ObjectConverter<T> {
    ContentValues convertObject(T object);
}

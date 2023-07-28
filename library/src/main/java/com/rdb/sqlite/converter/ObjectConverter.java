package com.rdb.sqlite.converter;

import com.rdb.sqlite.ContentValuesWrapper;
import com.rdb.sqlite.CursorWrapper;

public interface ObjectConverter<T> {
    void convertObject(ContentValuesWrapper contentValues, T object);

    T convertCursor(CursorWrapper cursor);
}

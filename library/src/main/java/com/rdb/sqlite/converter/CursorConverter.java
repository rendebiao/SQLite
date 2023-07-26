package com.rdb.sqlite.converter;

import android.database.Cursor;

public interface CursorConverter<T> {
    T convertCursor(Cursor cursor);
}

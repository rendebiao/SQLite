package com.rdb.sqlite;

import android.database.Cursor;

public interface CursorReader {

    void onReadCursor(Cursor cursor);
}

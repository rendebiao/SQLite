package com.rdb.sqlite;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CursorWrapper {

    Cursor cursor;

    public CursorWrapper(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean isNull(String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.isNull(index);
        }
        return true;
    }

    public int getIntFromCursor(String columnName, int defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getInt(index);
        }
        return defaultValue;
    }

    public long getLongFromCursor(String columnName, long defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getLong(index);
        }
        return defaultValue;
    }

    public short getShortFromCursor(String columnName, short defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getShort(index);
        }
        return defaultValue;
    }

    public byte[] getBlobFromCursor(String columnName, byte[] defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getBlob(index);
        }
        return defaultValue;
    }

    public double getDoubleFromCursor(String columnName, double defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getDouble(index);
        }
        return defaultValue;
    }

    public String getStringFromCursor(String columnName, String defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getString(index);
        }
        return defaultValue;
    }

    public float getFloatFromCursor(String columnName, float defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            return cursor.getFloat(index);
        }
        return defaultValue;
    }

    public JSONObject getJSONObjectFromCursor(String columnName, JSONObject defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            try {
                return new JSONObject(cursor.getString(index));
            } catch (JSONException e) {
                SQLite.log("getJSONObjectFromCursor", e);
            }
        }
        return defaultValue;
    }


    public JSONArray getJSONArrayFromCursor(String columnName, JSONArray defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0) {
            try {
                return new JSONArray(cursor.getString(index));
            } catch (JSONException e) {
                SQLite.log("getJSONArrayFromCursor", e);
            }
        }
        return defaultValue;
    }
}

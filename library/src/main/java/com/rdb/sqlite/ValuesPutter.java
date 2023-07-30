package com.rdb.sqlite;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

class ValuesPutter {

    private final ContentValues contentValues = new ContentValues();

    public void putNull(String key) {
        contentValues.put(key, (String) null);
    }

    public void putString(String key, String value) {
        contentValues.put(key, value);
    }

    public void putByte(String key, Byte value) {
        contentValues.put(key, value);
    }

    public void putShort(String key, Short value) {
        contentValues.put(key, value);
    }

    public void putInt(String key, Integer value) {
        contentValues.put(key, value);
    }

    public void putLong(String key, Long value) {
        contentValues.put(key, value);
    }

    public void putFloat(String key, Float value) {
        contentValues.put(key, value);
    }

    public void putDouble(String key, Double value) {
        contentValues.put(key, value);
    }

    public void putChar(String key, Character value) {
        if (value == null) {
            contentValues.putNull(key);
        } else {
            contentValues.put(key, (int) value.charValue());
        }
    }

    public void putBoolean(String key, Boolean value) {
        contentValues.put(key, value);
    }

    public void putBytes(String key, byte[] value) {
        contentValues.put(key, value);
    }

    public void putJSONObject(String key, JSONObject value) {
        contentValues.put(key, value == null ? null : value.toString());
    }

    public void putJSONArray(String key, JSONArray value) {
        contentValues.put(key, value == null ? null : value.toString());
    }

    public ContentValues getContentValues() {
        return contentValues;
    }

    public boolean containsKey(String key) {
        return contentValues.containsKey(key);
    }
}

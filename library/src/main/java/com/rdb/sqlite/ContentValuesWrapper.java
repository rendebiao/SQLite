package com.rdb.sqlite;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContentValuesWrapper {

    private ContentValues contentValues = new ContentValues();

    public void put(String key, String value) {
        contentValues.put(key, value);
    }

    public void put(String key, Byte value) {
        contentValues.put(key, value);
    }

    public void put(String key, Short value) {
        contentValues.put(key, value);
    }

    public void put(String key, Integer value) {
        contentValues.put(key, value);
    }

    public void put(String key, Long value) {
        contentValues.put(key, value);
    }

    public void put(String key, Float value) {
        contentValues.put(key, value);
    }

    public void put(String key, Double value) {
        contentValues.put(key, value);
    }

    public void put(String key, Boolean value) {
        contentValues.put(key, value);
    }

    public void put(String key, byte[] value) {
        contentValues.put(key, value);
    }

    public void put(String key, JSONObject value) {
        contentValues.put(key, value == null ? null : value.toString());
    }

    public void put(String key, JSONArray value) {
        contentValues.put(key, value == null ? null : value.toString());
    }

    public void putNull(String key) {
        contentValues.put(key, (String) null);
    }

    public ContentValues getContentValues() {
        return contentValues;
    }
}

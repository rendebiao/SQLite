package com.rdb.sqlite;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ValuesGetter {

    private static final String TAG = ValuesGetter.class.getSimpleName();
    private final Cursor cursor;

    public ValuesGetter(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean isNull(String key) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.isNull(index);
            }
        }
        return true;
    }

    public String getString(String key, String defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getString(index);
            }
        }
        return defaultValue;
    }

    public byte getByte(String key, byte defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return (byte) cursor.getInt(index);
            }
        }
        return defaultValue;
    }

    public short getShort(String key, short defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getShort(index);
            }
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getInt(index);
            }
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getLong(index);
            }
        }
        return defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getFloat(index);
            }
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getDouble(index);
            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getInt(index) == 1;
            }
        }
        return defaultValue;
    }

    public char getChar(String key, char defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return (char) cursor.getInt(index);
            }
        }
        return defaultValue;
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                return cursor.getBlob(index);
            }
        }
        return defaultValue;
    }


    public JSONObject getJSONObject(String key, JSONObject defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                try {
                    return new JSONObject(cursor.getString(index));
                } catch (JSONException e) {
                    SQLite.e(TAG, "getJSONObjectFromCursor", e);
                }
            }
        }
        return defaultValue;
    }

    public JSONArray getJSONArray(String key, JSONArray defaultValue) {
        if (cursor != null) {
            int index = cursor.getColumnIndex(key);
            if (index >= 0) {
                try {
                    return new JSONArray(cursor.getString(index));
                } catch (JSONException e) {
                    SQLite.e(TAG, "getJSONArrayFromCursor", e);
                }
            }
        }
        return defaultValue;
    }
}

package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rdb.sqlite.converter.ObjectConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class SQLiteOperator {

    public static boolean insert(SQLiteHelper helper, String tableName, ContentValues contentValues) {
        boolean insert = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            insert = dataBase.insert(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            SQLite.log("insert", e);
        }
        helper.closeDatabase();
        return insert;
    }

    public static boolean replace(SQLiteHelper helper, String tableName, ContentValues contentValues) {
        boolean replace = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            replace = dataBase.replace(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            SQLite.log("replace", e);
        }
        helper.closeDatabase();
        return replace;
    }

    public static boolean update(SQLiteHelper helper, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        boolean update = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            update = dataBase.update(tableName, values, whereClause, whereArgs) > 0;
        } catch (Exception e) {
            SQLite.log("update", e);
        }
        helper.closeDatabase();
        return update;
    }

    public static <T> boolean insert(SQLiteHelper helper, String tableName, T object, ObjectConverter<T> objectConverter) {
        boolean insert = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValuesWrapper contentValues = new ContentValuesWrapper();
        objectConverter.convertObject(contentValues, object);
        try {
            insert = dataBase.insert(tableName, null, contentValues.getContentValues()) > 0;
        } catch (Exception e) {
            SQLite.log("insert", e);
        }
        helper.closeDatabase();
        return insert;
    }

    public static <T> boolean replace(SQLiteHelper helper, String tableName, T object, ObjectConverter<T> objectConverter) {
        boolean replace = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValuesWrapper contentValues = new ContentValuesWrapper();
        objectConverter.convertObject(contentValues, object);
        try {
            replace = dataBase.replace(tableName, null, contentValues.getContentValues()) > 0;
        } catch (Exception e) {
            SQLite.log("replace", e);
        }
        helper.closeDatabase();
        return replace;
    }

    public static boolean delete(SQLiteHelper helper, String tableName, String whereClause, String[] whereArgs) {
        int delete = 0;
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            delete = dataBase.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            SQLite.log("delete", e);
        }
        helper.closeDatabase();
        return delete > 0;
    }

    public static <T> T queryObject(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, ObjectConverter<T> converter) {
        T object = null;
        SQLiteDatabase dataBase = helper.openDatabase();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                CursorWrapper cursorWrapper = new CursorWrapper(cursor);
                if (cursor.moveToFirst()) {
                    object = converter.convertCursor(cursorWrapper);
                }
            }
        } catch (Exception e) {
            SQLite.log("queryObject", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        helper.closeDatabase();
        return object;
    }

    public static <T> ArrayList<T> queryList(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, ObjectConverter<T> converter) {
        return queryList(helper, tableName, columns, selection, selectionArgs, null, null, null, converter);
    }

    public static <T> ArrayList<T> queryList(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, ObjectConverter<T> converter) {
        SQLiteDatabase dataBase = helper.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null) {
                CursorWrapper cursorWrapper = new CursorWrapper(cursor);
                while (cursor.moveToNext()) {
                    objects.add(converter.convertCursor(cursorWrapper));
                }
            }
        } catch (Exception e) {
            SQLite.log("queryList", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        helper.closeDatabase();
        return objects;
    }

    public static boolean execSQL(SQLiteHelper helper, String sql) {
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            dataBase.execSQL(sql);
            SQLite.log("execSQL=" + sql);
            return true;
        } catch (Exception e) {
            SQLite.log("execSQL", e);
        } finally {
            helper.closeDatabase();
        }
        return false;
    }

    public static boolean execSQL(SQLiteHelper helper, String sql, Object[] bindArgs) {
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            dataBase.execSQL(sql, bindArgs);
            return true;
        } catch (Exception e) {
            SQLite.log("execSQL", e);
        } finally {
            helper.closeDatabase();
        }
        return false;
    }

    public static boolean dropTable(SQLiteHelper helper, String tableName) {
        SQLiteDatabase dataBase = helper.openDatabase();
        try {
            dataBase.execSQL("DROP TABLE IF EXISTS " + tableName);
        } catch (Exception e) {
            SQLite.log("dropTable", e);
        } finally {
            helper.closeDatabase();
        }
        return false;
    }

    public static JSONArray toJson(SQLiteHelper helper, String tableName) {
        JSONArray array = new JSONArray();
        SQLiteDatabase dataBase = helper.openDatabase();
        Cursor cursor = dataBase.query(tableName, null, "1 = 1", new String[]{}, null, null, null);
        if (cursor != null) {
            int position = 0;
            while (cursor.moveToNext()) {
                JSONObject object = toJson(cursor);
                try {
                    array.put(position, object);
                    position++;
                } catch (JSONException e) {
                    SQLite.log("toJson", e);
                }
            }
            cursor.close();
        }
        helper.closeDatabase();
        return array;
    }

    public static JSONObject toJson(Cursor cursor) {
        JSONObject object = new JSONObject();
        int columnCount = cursor.getColumnCount();
        try {
            for (int i = 0; i < columnCount; i++) {
                object.put(cursor.getColumnName(i), cursor.getString(i));
            }
        } catch (JSONException e) {
            SQLite.log("toJson", e);
        }
        return object;
    }
}

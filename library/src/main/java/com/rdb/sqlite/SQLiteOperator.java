package com.rdb.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rdb.sqlite.converter.CursorConverter;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        helper.closeDatabase();
        return update;
    }

    public static <T> boolean insert(SQLiteHelper helper, String tableName, T object, ObjectConverter<T> objectConverter) {
        boolean insert = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValues contentValues = objectConverter.convertObject(object);
        try {
            insert = dataBase.insert(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.closeDatabase();
        return insert;
    }

    public static <T> boolean replace(SQLiteHelper helper, String tableName, T object, ObjectConverter<T> objectConverter) {
        boolean replace = false;
        SQLiteDatabase dataBase = helper.openDatabase();
        ContentValues contentValues = objectConverter.convertObject(object);
        try {
            replace = dataBase.replace(tableName, null, contentValues) > 0;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        helper.closeDatabase();
        return delete > 0;
    }

    public static <T> T queryObject(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        T object = null;
        SQLiteDatabase dataBase = helper.openDatabase();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    object = converter.convertCursor(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        helper.closeDatabase();
        return object;
    }

    public static <T> ArrayList<T> queryList(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, CursorConverter<T> converter) {
        return queryList(helper, tableName, columns, selection, selectionArgs, null, null, null, converter);
    }

    public static <T> ArrayList<T> queryList(SQLiteHelper helper, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, CursorConverter<T> converter) {
        SQLiteDatabase dataBase = helper.openDatabase();
        ArrayList<T> objects = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dataBase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    objects.add(converter.convertCursor(cursor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                    e.printStackTrace();
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
            e.printStackTrace();
        }
        return object;
    }
}

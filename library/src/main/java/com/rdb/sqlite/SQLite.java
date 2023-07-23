package com.rdb.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SQLite {

    private SQLiteHelper sqLiteHelper;
    private Map<String, Table> tableMap = new HashMap<>();

    public SQLite(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    public static JSONArray toJson(SQLiteDatabase dataBase, String tableName) {
        JSONArray array = new JSONArray();
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

    public Table table(String tableName) {
        Table table = tableMap.get(tableName);
        if (table == null) {
            table = new Table(sqLiteHelper, tableName);
            tableMap.put(tableName, table);
        }
        return table;
    }
}

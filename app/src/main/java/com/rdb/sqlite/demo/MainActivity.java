package com.rdb.sqlite.demo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rdb.sqlite.Column;
import com.rdb.sqlite.EntitySQLite;
import com.rdb.sqlite.EntityTable;
import com.rdb.sqlite.SQLite;
import com.rdb.sqlite.SQLiteHelper;
import com.rdb.sqlite.Table;
import com.rdb.sqlite.TableSQLBuilder;
import com.rdb.sqlite.converter.ObjectConverter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView resultView = findViewById(R.id.resultView);
        final User user = new User();
        user.id = "0";
        user.name = "888";
        user.age = 30;

        //Table
        SQLiteHelper helper = new SQLiteHelper(this, "db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String sql = new TableSQLBuilder("user").addPrimaryColumn("id", Column.TEXT, false)
                        .addColumn("name", Column.TEXT, false)
                        .addColumn("age", Column.INTEGER, false).build();
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        SQLite sqLite = new SQLite(helper);
        Table table = sqLite.table("user");
        table.insert(user, new ObjectConverter<User>() {
            @Override
            public ContentValues convertObject(User object) {
                ContentValues values = new ContentValues();
                values.put("id", object.id);
                values.put("name", object.name);
                values.put("age", object.age);
                return values;
            }
        });

        //EntityTable
        EntitySQLite entitySQLite = new EntitySQLite(this, "db", null);
        EntityTable<User> userTable = entitySQLite.table(User.class);
        userTable.insert(user);
    }

}

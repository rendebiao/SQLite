package com.rdb.sqlite.demo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rdb.sqlite.Column;
import com.rdb.sqlite.EntitySQLite;
import com.rdb.sqlite.EntityTable;
import com.rdb.sqlite.SQLite;
import com.rdb.sqlite.SQLiteHelper;
import com.rdb.sqlite.Table;
import com.rdb.sqlite.TableSQLBuilder;
import com.rdb.sqlite.converter.CursorConverter;
import com.rdb.sqlite.converter.ObjectConverter;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button insertView1;
    Button queryView1;
    Button insertView2;
    Button queryView2;
    TextView resultView;

    Table table;
    EntityTable<User> userTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertView1 = findViewById(R.id.insertView1);
        queryView1 = findViewById(R.id.queryView1);
        insertView2 = findViewById(R.id.insertView2);
        queryView2 = findViewById(R.id.queryView2);
        resultView = findViewById(R.id.resultView);

        //Table
        SQLiteHelper helper = new SQLiteHelper(this, "db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLite sqLite = new SQLite(helper);
        String sql = new TableSQLBuilder("user").addPrimaryColumn("id", Column.INTEGER, true)
                .addColumn("name", Column.TEXT, false)
                .addColumn("age", Column.INTEGER, false).build();
        sqLite.execSQL(sql);
        table = sqLite.table("user");

        //EntityTable
        EntitySQLite entitySQLite = new EntitySQLite(this, "entity", null);
        userTable = entitySQLite.table(User.class);

        final User user = new User();
        user.name = "name";
        user.age = 30;

        insertView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.id++;
                table.insert(user, new ObjectConverter<User>() {
                    @Override
                    public ContentValues convertObject(User object) {
                        ContentValues values = new ContentValues();
//                        values.put("id", object.id);
                        values.put("name", object.name);
                        values.put("age", object.age);
                        return values;
                    }
                });
            }
        });

        queryView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> result = table.queryList(new String[]{"id", "name", "age"}, "1=1", null, new CursorConverter<User>() {
                    @Override
                    public User convertCursor(Cursor cursor) {
                        User user1 = new User();
                        int columnIndex = cursor.getColumnIndex("id");
                        if (columnIndex >= 0) {
                            user1.id = cursor.getLong(columnIndex);
                        }
                        columnIndex = cursor.getColumnIndex("name");
                        if (columnIndex >= 0) {
                            user1.name = cursor.getString(columnIndex);
                        }
                        columnIndex = cursor.getColumnIndex("age");
                        if (columnIndex >= 0) {
                            user1.age = cursor.getInt(columnIndex);
                        }
                        return user1;
                    }
                });
                resultView.setText("SQLite:\n" + result.toString());
            }
        });

        insertView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTable.insert(user);
            }
        });

        queryView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> result = userTable.queryList("1=1", new String[]{});
                resultView.setText("EntitySQLite:\n" + result.toString());
            }
        });
    }
}
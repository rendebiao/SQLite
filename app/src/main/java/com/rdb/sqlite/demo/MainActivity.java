package com.rdb.sqlite.demo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rdb.sqlite.Column;
import com.rdb.sqlite.ContentValuesWrapper;
import com.rdb.sqlite.CursorWrapper;
import com.rdb.sqlite.EntitySQLite;
import com.rdb.sqlite.EntityTable;
import com.rdb.sqlite.SQLite;
import com.rdb.sqlite.SQLiteHelper;
import com.rdb.sqlite.Table;
import com.rdb.sqlite.TableSQLBuilder;
import com.rdb.sqlite.converter.FeildConverter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        EntitySQLite.checkEntityClass(User.class);

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
                .addColumn("age", Column.INTEGER, false)
                .addColumn("address", Column.TEXT, false).build();
        sqLite.execSQL(sql);
        table = sqLite.table("user");

        EntitySQLite.registerFeildConverter(Address.class.getName(), new FeildConverter<Address>() {
            @Override
            public Address cursorToFieldValue(@NonNull CursorWrapper cursor, @NonNull String fieldName) throws IllegalAccessException {
                JSONObject jsonObject = cursor.getJSONObjectFromCursor(fieldName, null);
                if (jsonObject != null) {
                    Address address = new Address();
                    address.name = jsonObject.optString("name");
                    address.postalCode = jsonObject.optString("postalCode");
                    return address;
                }
                return null;
            }

            @Override
            public void fieldValueToContentValues(@NonNull ContentValuesWrapper contentValues, String fieldName, @NonNull Address fieldValue) {
                if (fieldValue != null) {
                    Map map = new HashMap();
                    map.put("name", fieldValue.name);
                    map.put("postalCode", fieldValue.postalCode);
                    JSONObject jsonObject = new JSONObject(map);
                    contentValues.put(fieldName, jsonObject);
                }
            }

            @Override
            public String getColumnType() {
                return Column.TEXT;
            }
        });
        //EntityTable
        EntitySQLite entitySQLite = new EntitySQLite(this, "entity", null);
        userTable = entitySQLite.table(User.class);

        final User user = new User(0, "name", 26, new Address("武汉市", "11111"));

        UserConverter converter = new UserConverter();

        insertView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.id++;
                table.insert(user, converter);
            }
        });

        queryView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> result = table.queryList(new String[]{"id", "name", "age", "address"}, "1=1", null, converter);
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
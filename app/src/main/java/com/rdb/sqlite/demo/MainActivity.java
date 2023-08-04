package com.rdb.sqlite.demo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.rdb.sqlite.DataType;
import com.rdb.sqlite.EntityTable;
import com.rdb.sqlite.HistoryEntity;
import com.rdb.sqlite.JsonConverter;
import com.rdb.sqlite.ObjectReader;
import com.rdb.sqlite.SQLite;
import com.rdb.sqlite.Table;
import com.rdb.sqlite.TableSQLBuilder;
import com.rdb.sqlite.ValuesGetter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button insertView1;
    Button queryView1;
    Button insertView2;
    Button queryView2;
    Button queryView3;
    TextView resultView;

    Table table1;
    EntityTable table2;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertView1 = findViewById(R.id.insertView1);
        queryView1 = findViewById(R.id.queryView1);
        insertView2 = findViewById(R.id.insertView2);
        queryView2 = findViewById(R.id.queryView2);
        queryView3 = findViewById(R.id.queryView3);
        resultView = findViewById(R.id.resultView);

        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(this, "db", null, 1, null) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String sql = new TableSQLBuilder("user").addPrimaryColumn("id", DataType.INTEGER, true).addColumn("name", DataType.TEXT, false).addColumn("age", DataType.INTEGER, false).addColumn("address", DataType.TEXT, true).build();
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.getOrCreateHistoryClass(UserEntity.class)
                .putClass(0, UserEntity.UserEntity0.class)
                .putClass(1, UserEntity.UserEntity1.class);

        //Table
        SQLite sqLite = new SQLite(openHelper, historyEntity, new JsonConverter() {
            @Override
            public String toJson(Object object) {
                return gson.toJson(object);
            }

            @Override
            public <T> T fromJson(String json, Type typeOfT) {
                return gson.fromJson(json, typeOfT);
            }
        });

        sqLite.checkClass(UserEntity.class);

        final User user1 = new User(0, "name", 26, new Address("武汉市", "11111"));
        final UserEntity user2 = new UserEntity(0, "name", 26, new Address("武汉市", "11111"));
//        final UserEntity user2 = new UserEntity(0, "name", 26, new Address("武汉市", "11111"));
        table1 = sqLite.getTable("user");
        table2 = sqLite.getEntityTable(UserEntity.class);
        insertView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name", user1.name);
                values.put("age", user1.age);
                JSONStringer stringer = new JSONStringer();
                try {
                    stringer.object();
                    stringer.key("name").value(user1.address.name);
                    stringer.key("postalCode").value(user1.address.postalCode);
                    stringer.endObject();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                values.put("address", stringer.toString());
                table1.insert(values);
            }
        });
        insertView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table2.insert(user2);
            }
        });

        queryView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> result = table1.queryAll(new ObjectReader<User>() {
                    @Override
                    public User readColumn(int position, ValuesGetter valuesGetter) {
                        User user = new User();
                        user.id = valuesGetter.getLong("id", 0);
                        user.name = valuesGetter.getString("name", null);
                        user.age = valuesGetter.getInt("age", 0);
                        if (!valuesGetter.isNull("address")) {
                            JSONObject object = valuesGetter.getJSONObject("address", null);
                            if (object != null) {
                                user.address = new Address();
                                user.address.name = object.optString("name");
                                user.address.postalCode = object.optString("postalCode");
                            }
                        }
                        return user;
                    }
                });
                resultView.setText("SQLite:\n" + gson.toJson(result));
            }
        });

        queryView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UserEntity> result = table2.queryAll();
                resultView.setText("SQLite:\n" + gson.toJson(result));
            }
        });

        queryView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> result = sqLite.getTableNames();
                resultView.setText("SQLite:\n" + gson.toJson(result));
            }
        });
    }
}
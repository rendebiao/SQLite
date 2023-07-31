# SQLite

SQLite封装

1.初始化

    SQLiteOpenHelper openHelper = ...;//原生SQLiteOpenHelper实现
    SQLite sqLite = new SQLite(openHelper);//创建SQLite对象

如果需要支持直接操作对象，需要进行如下初始化

    JsonConverter jsonConverter = ...;//类中复杂属性将通过转换成json存入数据库，需要json和对象互相转换的能力
    SQLiteLinstener sqliteLinstener = ...;//当Class发生变法导致表结构变化时 通过此监听处理历史数据
    sqLite.init(jsonConverter，sqliteLinstener);

2.初级使用

建表：

    String sql = new TableSQLBuilder("user")
        .addPrimaryColumn("id", DataType.INTEGER, true)
        .addColumn("name", DataType.TEXT, false)
        .addColumn("age", DataType.INTEGER, false)
        .addColumn("address", DataType.TEXT, false)
        .build();
    db.execSQL(sql);

获取表：

    Table table = sqLite.table("user");

插入数据：

    ContentValues values = new ContentValues();
    values.put("name", user.name);
    values.put("age", user.age);
    JSONStringer stringer = new JSONStringer();
    try {
        stringer.object();
        stringer.key("name").value(user.address.name);
        stringer.key("postalCode").value(user.address.postalCode);
        stringer.endObject();
    } catch (JSONException e) {
        throw new RuntimeException(e);
    }
    values.put("address", stringer.toString());
    table.insert(values);

查询数据：

    List<User> result = table1.queryAll(new ObjectReader<User>() {
        @Override
        public User readColumn(int position, ValuesGetter valuesGetter) {
            User user = new User();
            user.id = valuesGetter.getLong("id", 0);
            user.name = valuesGetter.getString("name", null);
            user.age = valuesGetter.getInt("age", 0);
            JSONObject object = valuesGetter.getJSONObject("address", null);
            if (object != null) {
                user.address = new Address();
                user.address.name = object.optString("name");
                user.address.postalCode = object.optString("postalCode");
            }
            return user;
        }
    });

2.高级使用

注解：

给实体类添加注解，EntityClass注解实体类，EntityColumn注解属性，根据注解会自动创建对应表。

    @EntityClass(version = 0, autoCreateTable = true)//版本为0，自动创建表
    public class User {

        @EntityColumn(primary = true, autoIncrement = true)//主键 自增
        public long id;

        public String name;//不加注解 默认 非主键 非空 非自增

        public int age;

        @EntityColumn(nullable = true)//可空
        public Address address;

        @EntityColumn(hidden = true)//隐藏的字段 不会关联数据库
        public String other;

        public User() {//必须要有空构造或默认空构造
        }
    }

创建表：

如已注解autoCreateTable = true，不需要手动建表，否则同初级使用中方法建表。

获取表：

    Table table = sqLite.table(User.class);

插入数据：

    table.insert(user);

查询数据：

    List<User> result = table.queryAll(User.class);

实体类升级：

将旧的User类改名为User0，新的类继续使用User，并将User中的version加一。

应用启动时将通过SQLiteLinstener收到回调

    public void onTableAlteredByClassChanged(Class tClass, Table alterTable) {
        List<User0> user0s = alterTable.queryAll(User0.class);//使用旧的类查询所有数据
        Table table = sqLite.table(User.class);//获取新Table
        for (User0 user0 : user0s) {
            table.insert(new User(user0));//遍历将User0转换成User对象插入数据库  完成数据升级
        }
    }

注意：实体类必须使用EntityClass注解，其有效属性数量必须大于0，有空构造，SQLite有初始化JsonConverter。

[![](https://www.jitpack.io/v/rendebiao/SQLite.svg)](https://www.jitpack.io/#rendebiao/SQLite)

# SQLite
SQLite封装

1.初级使用

    初始化：
    SQLiteOpenHelper openHelper = ...;//原生SQLiteOpenHelper实现
    SQLite sqLite = new SQLite(openHelper);//创建SQLite对象

    建表：
    String sql = new TableSQLBuilder("user")
        .addPrimaryColumn("id", DataType.INTEGER, true)
        .addColumn("name", DataType.TEXT, false)
        .addColumn("age", DataType.INTEGER, false)
        .addColumn("address", DataType.TEXT, false)
        .build();
    db.execSQL(sql);

    获取表：
    Table table = sqLite.getTable("user");

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
    给实体类添加注解，Entity注解实体类，EntityColumn注解属性，根据注解会自动创建对应表。

    @Entity(version = 0)//版本为0可不加注解
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

    初始化：
    SQLiteOpenHelper openHelper = ...;//原生SQLiteOpenHelper实现
    HistoryEntity historyEntity = new HistoryEntity();//历史实体类
    historyEntity.newHistoryClass(UserEntity.class).putClass(0, UserEntity.UserEntity0.class).putClass(1, UserEntity.UserEntity1.class);//给实体类设置历史类 用于历史数据升级
    JsonConverter jsonConverter = ...;//类中复杂属性将通过转换成json存入数据库，需要json和对象互相转换的能力
    SQLite sqLite = new SQLite(openHelper, historyEntity, jsonConverter);//创建SQLite对象, jsonConverter必须设置

    获取表：
    EntityTable table = sqLite.getEntityTable(User.class);

    插入数据：
    table.insert(user);

    查询数据：
    List<User> result = table.queryAll(User.class);

    实体类升级：
    以0升级到1为例：
    1.将旧的UserEntity类备份改名为UserEntity0，
    2.按照需要修改原UserEntity类结构，将其Entity注解的version修改为1
    3.使UserEntity0实现HistoryConverter<UserEntity>接口，实现UserEntity toCurrent()方法，支持将UserEntity0对象转换成UserEntity对象，注意UserEntity新增属性如果注解为非空，需要赋予默认值.
    4.初始化时将历史类写入historyEntity：historyEntity.newHistoryClass(UserEntity.class).putClass(0, UserEntity.UserEntity0.class)。

    注意：实体类有效属性数量必须大于0，有空构造，SQLite有初始化JsonConverter。

[![](https://www.jitpack.io/v/rendebiao/SQLite.svg)](https://www.jitpack.io/#rendebiao/SQLite)

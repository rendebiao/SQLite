# SQLite

SQLite封装
SQLite存放普通数据和对象，表需要手动创建，对象通过转化器转化（CursorConverter、ObjectConverter）。

EntitySQLite直接存放对象，对象属性支持基本数据类型，基本数据类型的封装类型、byte[]。
可以通过EntitySQLite.registerEntityColumnConverter支持其他类型。


[![](https://www.jitpack.io/v/rendebiao/SQLite.svg)](https://www.jitpack.io/#rendebiao/SQLite)
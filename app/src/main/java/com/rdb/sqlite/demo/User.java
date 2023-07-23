package com.rdb.sqlite.demo;

import com.rdb.sqlite.entity.annotation.EntityColumn;
import com.rdb.sqlite.entity.annotation.EntityVersion;

@EntityVersion(value = 0)
public class User {

    @EntityColumn(hide = false, primary = true, nullable = false)
    public String id;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public String name;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public int age;
}

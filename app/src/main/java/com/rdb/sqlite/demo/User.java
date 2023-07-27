package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.annotation.EntityVersion;

@EntityVersion(value = 0)
public class User {

    @EntityColumn(hide = false, primary = true, nullable = false, autoIncrement = true)
    public long id;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public String name;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public int age;

    @NonNull
    @Override
    public String toString() {
        return "id=" + id + ",name=" + name + ",age=" + age;
    }
}

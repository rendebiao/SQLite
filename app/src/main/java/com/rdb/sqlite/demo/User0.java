package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

@EntityClass(version = 0, autoCreateTable = true)
public class User0 {

    @EntityColumn(primary = true, autoIncrement = true)
    public long id;

    public String name;

    public int age;

    public User0() {
    }

    public User0(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{id=" + id + ",name=" + name + ",age=" + age + "}";
    }
}

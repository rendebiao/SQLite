package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.annotation.EntityVersion;

@EntityVersion(value = 1)
public class User {

    @EntityColumn(hide = false, primary = true, nullable = false, autoIncrement = true)
    public long id;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public String name;

    @EntityColumn(hide = false, primary = false, nullable = false)
    public int age;

    @EntityColumn(hide = false, primary = false, nullable = true)
    public Address address;

    public User() {
    }

    public User(long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{id=" + id + ",name=" + name + ",age=" + age + ",address=" + address + "}";
    }
}

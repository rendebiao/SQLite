package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

@EntityClass(version = 1, autoCreateTable = true)
public class User {

    @EntityColumn(primary = true, autoIncrement = true)
    public long id;

    public String name;

    public int age;

    @EntityColumn(nullable = true)
    public Address address;

    public User() {
    }

    public User(long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public User(User0 user0) {
        this.id = user0.id;
        this.name = user0.name;
        this.age = user0.age;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{id=" + id + ",name=" + name + ",age=" + age + ",address=" + address + "}";
    }
}

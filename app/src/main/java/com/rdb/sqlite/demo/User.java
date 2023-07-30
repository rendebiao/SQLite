package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

import java.util.List;
import java.util.Map;

@EntityClass(version = 1, autoCreateTable = true)
public class User {

    @EntityColumn(primary = true, autoIncrement = true)
    public long id;

    public String name;

    public int age;

    @EntityColumn(nullable = true)
    public Address address;

    @EntityColumn(nullable = true)
    public List<Address> addresses1;

    @EntityColumn(nullable = true)
    public Map<String, Address> addresses2;

    @EntityColumn(nullable = true)
    public Address[] addresses3;

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

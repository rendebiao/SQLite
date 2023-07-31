package com.rdb.sqlite.demo;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

@EntityClass(version = 2)
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

    public User(User1 user1) {
        this.id = user1.id;
        this.name = user1.name;
        this.age = user1.age;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{id=" + id + ",name=" + name + ",age=" + age + ",address=" + address + "}";
    }

    @EntityClass(version = 0)
    public static class User0 {

        @EntityColumn(primary = true, autoIncrement = true)
        public long id;

        public String name;

        public int age;

        public User0() {
        }
    }


    @EntityClass(version = 1)
    public static class User1 {

        @EntityColumn(primary = true, autoIncrement = true)
        public long id;

        public String name;

        public int age;

        public String address;

        public User1() {
        }
    }
}

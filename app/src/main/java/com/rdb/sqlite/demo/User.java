package com.rdb.sqlite.demo;

public class User {

    public long id;

    public String name;

    public int age;

    public Address address;

    public User() {
    }

    public User(long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
}

package com.rdb.sqlite.demo;

public class Address {

    String name;
    String postalCode;

    public Address() {
    }

    public Address(String name, String postalCode) {
        this.name = name;
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "Address{name=" + name + ",postalCode=" + postalCode + "}";
    }
}

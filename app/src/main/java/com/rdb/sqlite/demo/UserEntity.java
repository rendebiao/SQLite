package com.rdb.sqlite.demo;

import com.rdb.sqlite.HistoryConverter;
import com.rdb.sqlite.annotation.Entity;
import com.rdb.sqlite.annotation.EntityColumn;

@Entity(version = 2)
public class UserEntity {

    @EntityColumn(primary = true, autoIncrement = true)
    public long id;

    public String name;

    public int age;

    @EntityColumn(nullable = true)
    public Address address;

    public UserEntity() {
    }

    public UserEntity(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public UserEntity(long id, String name, int age, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public UserEntity(UserEntity0 user0) {
        this.id = user0.id;
        this.name = user0.name;
        this.age = user0.age;
    }

    public UserEntity(UserEntity1 user1) {
        this.id = user1.id;
        this.name = user1.name;
        this.age = user1.age;
    }

    @Entity(version = 0)
    public static class UserEntity0 implements HistoryConverter<UserEntity> {

        @EntityColumn(primary = true, autoIncrement = true)
        public long id;

        public String name;

        public int age;

        public UserEntity0() {
        }

        @Override
        public UserEntity toCurrent() {
            return new UserEntity(this);
        }
    }


    @Entity(version = 1)
    public static class UserEntity1 implements HistoryConverter<UserEntity> {

        @EntityColumn(primary = true, autoIncrement = true)
        public long id;

        public String name;

        public int age;

        public String address;

        public UserEntity1() {
        }

        @Override
        public UserEntity toCurrent() {
            return new UserEntity(this);
        }
    }
}

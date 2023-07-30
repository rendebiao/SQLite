package com.rdb.sqlite;

import java.lang.reflect.Type;

public interface JsonConverter {

    String toJson(Object object);

    <T> T fromJson(String json, Type typeOfT);
}

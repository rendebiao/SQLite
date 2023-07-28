package com.rdb.sqlite.demo;

import com.rdb.sqlite.ContentValuesWrapper;
import com.rdb.sqlite.CursorWrapper;
import com.rdb.sqlite.annotation.EntityVersion;
import com.rdb.sqlite.converter.ObjectConverter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EntityVersion(value = 0)
public class UserConverter implements ObjectConverter<User> {


    @Override
    public void convertObject(ContentValuesWrapper contentValues, User object) {
        if (object.address != null) {
            Map map = new HashMap();
            map.put("name", object.address.name);
            map.put("postalCode", object.address.postalCode);
            JSONObject jsonObject = new JSONObject(map);
            contentValues.put("address", jsonObject);
        }
        contentValues.put("name", object.name);
        contentValues.put("age", object.age);
    }

    @Override
    public User convertCursor(CursorWrapper cursor) {
        User user = new User(0, null, 0, null);
        user.id = cursor.getLongFromCursor("id", 0);
        user.name = cursor.getStringFromCursor("name", null);
        user.age = cursor.getIntFromCursor("age", 0);
        JSONObject object = cursor.getJSONObjectFromCursor("address", null);
        if (object != null) {
            user.address = new Address();
            user.address.name = object.optString("name");
            user.address.postalCode = object.optString("postalCode");
        }
        return user;
    }
}

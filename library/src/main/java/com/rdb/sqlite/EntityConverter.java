package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityColumn;
import com.rdb.sqlite.converter.FeildConverter;
import com.rdb.sqlite.converter.ObjectConverter;

import java.lang.reflect.Field;
import java.util.List;

class EntityConverter<T> implements ObjectConverter<T> {

    private final Class<T> tClass;

    public EntityConverter(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T convertCursor(CursorWrapper cursor) {
        T object = null;
        try {
            object = tClass.newInstance();
            List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
            for (Field field : fields) {
                if (Entity.isColumn(field)) {
                    try {
                        field.setAccessible(true);
                        FeildConverter converter = Entity.getFeildConverter(field.getType().getName());
                        if ((converter == null) || cursor.isNull(field.getName())) {
                            field.set(object, null);
                        } else {
                            field.set(object, converter.cursorToFieldValue(cursor, field.getName()));
                        }
                    } catch (Exception e) {
                        EntitySQLite.log("cursorToFieldValue", e);
                    }
                }
            }
        } catch (Exception e) {
            EntitySQLite.log("convertCursor", e);
        }
        return object;
    }


    @Override
    public void convertObject(ContentValuesWrapper contentValues, T object) {
        List<Field> fields = Entity.getUnStaticDeclaredFields(tClass);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            EntityColumn column = field.getAnnotation(EntityColumn.class);
            if (column == null || (!column.hide() && !column.autoIncrement())) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);
                    FeildConverter converter = Entity.getFeildConverter(field.getType().getName());
                    if (converter == null || fieldValue == null || (column != null && column.autoIncrement() && fieldValue == Integer.valueOf(0))) {
                        contentValues.putNull(field.getName());
                    } else {
                        converter.fieldValueToContentValues(contentValues, field.getName(), fieldValue);
                    }
                } catch (Exception e) {
                    EntitySQLite.log("fieldValueToContentValues", e);
                }
            }
        }
    }
}

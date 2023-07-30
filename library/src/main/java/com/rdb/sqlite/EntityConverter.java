package com.rdb.sqlite;

import androidx.annotation.NonNull;

import com.rdb.sqlite.annotation.EntityColumn;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

class EntityConverter {

    private static final Map<String, FieldConverter> FIELD_CONVERTERS = new HashMap<>();

    static {
        FIELD_CONVERTERS.put(Byte.TYPE.getName(), FieldConverter.BYTE_CONVERTER);
        FIELD_CONVERTERS.put(Byte.class.getName(), FieldConverter.BYTE_CONVERTER);
        FIELD_CONVERTERS.put(Short.TYPE.getName(), FieldConverter.SHORT_CONVERTER);
        FIELD_CONVERTERS.put(Short.class.getName(), FieldConverter.SHORT_CONVERTER);
        FIELD_CONVERTERS.put(Integer.class.getName(), FieldConverter.INTEGER_CONVERTER);
        FIELD_CONVERTERS.put(Integer.TYPE.getName(), FieldConverter.INTEGER_CONVERTER);
        FIELD_CONVERTERS.put(Long.class.getName(), FieldConverter.LONG_CONVERTER);
        FIELD_CONVERTERS.put(Long.TYPE.getName(), FieldConverter.LONG_CONVERTER);
        FIELD_CONVERTERS.put(Float.class.getName(), FieldConverter.FLOAT_CONVERTER);
        FIELD_CONVERTERS.put(Float.TYPE.getName(), FieldConverter.FLOAT_CONVERTER);
        FIELD_CONVERTERS.put(Double.class.getName(), FieldConverter.DOUBLE_CONVERTER);
        FIELD_CONVERTERS.put(Double.TYPE.getName(), FieldConverter.DOUBLE_CONVERTER);
        FIELD_CONVERTERS.put(Boolean.class.getName(), FieldConverter.BOOLEAN_CONVERTER);
        FIELD_CONVERTERS.put(Boolean.TYPE.getName(), FieldConverter.BOOLEAN_CONVERTER);
        FIELD_CONVERTERS.put(Character.class.getName(), FieldConverter.CHARACTER_CONVERTER);
        FIELD_CONVERTERS.put(Character.TYPE.getName(), FieldConverter.CHARACTER_CONVERTER);
        FIELD_CONVERTERS.put(String.class.getName(), FieldConverter.STRING_CONVERTER);
    }

    private final JsonConverter jsonConverter;

    public EntityConverter(@NonNull JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public static DataType getDataType(Field field) {
        FieldConverter converter = FIELD_CONVERTERS.get(field.getType().getName());
        return converter == null ? DataType.TEXT : converter.getDataType();
    }

    public <T> void convert(T entity, @NonNull ValuesPutter valuesPutter) {
        if (entity != null) {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    EntityColumn entityColumn = field.getAnnotation(EntityColumn.class);
                    if (entityColumn == null || (!entityColumn.hidden() && !entityColumn.autoIncrement())) {
                        Object fieldValue = null;
                        try {
                            field.setAccessible(true);
                            fieldValue = field.get(entity);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        if (fieldValue == null) {
                            valuesPutter.putNull(field.getName());
                        } else {
                            FieldConverter converter = FIELD_CONVERTERS.get(field.getType().getName());
                            if (converter == null) {
                                valuesPutter.putString(field.getName(), jsonConverter.toJson(fieldValue));
                            } else {
                                converter.fieldToValue(valuesPutter, field.getName(), fieldValue);
                            }
                        }
                    }
                }
            }
        }
    }

    public <T> void convert(T entity, @NonNull ValuesGetter valuesGetter) {
        if (entity != null) {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    EntityColumn entityColumn = field.getAnnotation(EntityColumn.class);
                    if (entityColumn == null || !entityColumn.hidden()) {
                        Object fieldValue;
                        if (valuesGetter.isNull(field.getName())) {
                            fieldValue = null;
                        } else {
                            FieldConverter converter = FIELD_CONVERTERS.get(field.getType().getName());
                            if (converter == null) {
                                String value = valuesGetter.getString(field.getName(), null);
                                fieldValue = jsonConverter.fromJson(value, field.getGenericType());
                            } else {
                                fieldValue = converter.valueToField(valuesGetter, field.getName());
                            }
                        }
                        try {
                            field.setAccessible(true);
                            field.set(entity, fieldValue);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public interface FieldConverter<T> {
        FieldConverter<Byte> BYTE_CONVERTER = new FieldConverter<Byte>() {
            @Override
            public Byte valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getByte(fieldName, (byte) 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Byte fieldValue) {
                putter.putByte(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<Short> SHORT_CONVERTER = new FieldConverter<Short>() {
            @Override
            public Short valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getShort(fieldName, (short) 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Short fieldValue) {
                putter.putShort(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<Integer> INTEGER_CONVERTER = new FieldConverter<Integer>() {
            @Override
            public Integer valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getInt(fieldName, 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Integer fieldValue) {
                putter.putInt(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<Long> LONG_CONVERTER = new FieldConverter<Long>() {
            @Override
            public Long valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getLong(fieldName, 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Long fieldValue) {
                putter.putLong(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<Float> FLOAT_CONVERTER = new FieldConverter<Float>() {
            @Override
            public Float valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getFloat(fieldName, (float) 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Float fieldValue) {
                putter.putFloat(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.REAL;
            }
        };
        FieldConverter<Double> DOUBLE_CONVERTER = new FieldConverter<Double>() {
            @Override
            public Double valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getDouble(fieldName, 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Double fieldValue) {
                putter.putDouble(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.REAL;
            }
        };
        FieldConverter<Character> CHARACTER_CONVERTER = new FieldConverter<Character>() {
            @Override
            public Character valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getChar(fieldName, (char) 0);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Character fieldValue) {
                putter.putChar(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<Boolean> BOOLEAN_CONVERTER = new FieldConverter<Boolean>() {
            @Override
            public Boolean valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getBoolean(fieldName, false);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull Boolean fieldValue) {
                putter.putBoolean(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.INTEGER;
            }
        };
        FieldConverter<String> STRING_CONVERTER = new FieldConverter<String>() {
            @Override
            public String valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName) {
                return getter.getString(fieldName, null);
            }

            @Override
            public void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull String fieldValue) {
                putter.putString(fieldName, fieldValue);
            }

            @NonNull
            @Override
            public DataType getDataType() {
                return DataType.TEXT;
            }
        };


        T valueToField(@NonNull ValuesGetter getter, @NonNull String fieldName);

        void fieldToValue(@NonNull ValuesPutter putter, @NonNull String fieldName, @NonNull T fieldValue);

        @NonNull
        DataType getDataType();
    }
}

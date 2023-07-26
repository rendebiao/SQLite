package com.rdb.sqlite.converter;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.rdb.sqlite.Column;

import java.lang.reflect.Field;

public interface EntityColumnConverter<T> {

    EntityColumnConverter<Character> CHAR_CONVERTER = new EntityColumnConverter<Character>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setChar(object, (char) cursor.getInt(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Character fieldValue) {
            contentValues.put(fieldName, (int) fieldValue.charValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Byte> BYTE_CONVERTER = new EntityColumnConverter<Byte>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setByte(object, (byte) cursor.getInt(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Byte fieldValue) {
            contentValues.put(fieldName, (int) fieldValue.byteValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Short> SHORT_CONVERTER = new EntityColumnConverter<Short>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setShort(object, cursor.getShort(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Short fieldValue) {
            contentValues.put(fieldName, fieldValue.shortValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Integer> INTEGER_CONVERTER = new EntityColumnConverter<Integer>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setInt(object, cursor.getInt(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Integer fieldValue) {
            contentValues.put(fieldName, fieldValue.intValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Long> LONG_CONVERTER = new EntityColumnConverter<Long>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setLong(object, cursor.getLong(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Long fieldValue) {
            contentValues.put(fieldName, fieldValue.longValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Float> FLOAT_CONVERTER = new EntityColumnConverter<Float>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setFloat(object, cursor.getFloat(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Float fieldValue) {
            contentValues.put(fieldName, fieldValue.floatValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Double> DOUBLE_CONVERTER = new EntityColumnConverter<Double>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setDouble(object, cursor.getDouble(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Double fieldValue) {
            contentValues.put(fieldName, fieldValue.doubleValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<Boolean> BOOLEAN_CONVERTER = new EntityColumnConverter<Boolean>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.setBoolean(object, cursor.getInt(columnIndex) == 1);
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, Boolean fieldValue) {
            contentValues.put(fieldName, fieldValue.booleanValue() ? 1 : 0);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    EntityColumnConverter<byte[]> BYTES_CONVERTER = new EntityColumnConverter<byte[]>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.set(object, cursor.getBlob(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, byte[] fieldValue) {
            contentValues.put(fieldName, fieldValue);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.BLOB;
        }
    };
    EntityColumnConverter<String> STRING_CONVERTER = new EntityColumnConverter<String>() {
        @Override
        public void cursorToFieldValue(Cursor cursor, int columnIndex, Object object, Field field) throws IllegalAccessException {
            field.set(object, cursor.getString(columnIndex));
        }

        @Override
        public void fieldValueToContentValues(ContentValues contentValues, String fieldName, String fieldValue) {
            contentValues.put(fieldName, fieldValue);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.TEXT;
        }
    };

    void cursorToFieldValue(@NonNull Cursor cursor, int columnIndex, @NonNull Object object, @NonNull Field field) throws IllegalAccessException;

    void fieldValueToContentValues(@NonNull ContentValues contentValues, String fieldName, @NonNull T fieldValue);

    @Column.Type
    String getColumnType();
}

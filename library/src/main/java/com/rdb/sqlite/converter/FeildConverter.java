package com.rdb.sqlite.converter;

import androidx.annotation.NonNull;

import com.rdb.sqlite.Column;
import com.rdb.sqlite.ContentValuesWrapper;
import com.rdb.sqlite.CursorWrapper;

public interface FeildConverter<T> {

    FeildConverter<Character> CHAR_CONVERTER = new FeildConverter<Character>() {
        @Override
        public Character cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return (char) cursor.getIntFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Character fieldValue) {
            contentValues.put(fieldName, (int) fieldValue.charValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Byte> BYTE_CONVERTER = new FeildConverter<Byte>() {
        @Override
        public Byte cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return (byte) cursor.getIntFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Byte fieldValue) {
            contentValues.put(fieldName, (int) fieldValue.byteValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Short> SHORT_CONVERTER = new FeildConverter<Short>() {
        @Override
        public Short cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getShortFromCursor(fieldName, (short) 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Short fieldValue) {
            contentValues.put(fieldName, fieldValue.shortValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Integer> INTEGER_CONVERTER = new FeildConverter<Integer>() {
        @Override
        public Integer cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getIntFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Integer fieldValue) {
            contentValues.put(fieldName, fieldValue.intValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Long> LONG_CONVERTER = new FeildConverter<Long>() {
        @Override
        public Long cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getLongFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Long fieldValue) {
            contentValues.put(fieldName, fieldValue.longValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Float> FLOAT_CONVERTER = new FeildConverter<Float>() {
        @Override
        public Float cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getFloatFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Float fieldValue) {
            contentValues.put(fieldName, fieldValue.floatValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Double> DOUBLE_CONVERTER = new FeildConverter<Double>() {
        @Override
        public Double cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getDoubleFromCursor(fieldName, 0);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Double fieldValue) {
            contentValues.put(fieldName, fieldValue.doubleValue());
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<Boolean> BOOLEAN_CONVERTER = new FeildConverter<Boolean>() {
        @Override
        public Boolean cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getIntFromCursor(fieldName, 0) == 1;
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, Boolean fieldValue) {
            contentValues.put(fieldName, fieldValue.booleanValue() ? 1 : 0);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.INTEGER;
        }
    };
    FeildConverter<byte[]> BYTES_CONVERTER = new FeildConverter<byte[]>() {
        @Override
        public byte[] cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getBlobFromCursor(fieldName, null);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, byte[] fieldValue) {
            contentValues.put(fieldName, fieldValue);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.BLOB;
        }
    };
    FeildConverter<String> STRING_CONVERTER = new FeildConverter<String>() {
        @Override
        public String cursorToFieldValue(CursorWrapper cursor, String fieldName) throws IllegalAccessException {
            return cursor.getStringFromCursor(fieldName, null);
        }

        @Override
        public void fieldValueToContentValues(ContentValuesWrapper contentValues, String fieldName, String fieldValue) {
            contentValues.put(fieldName, fieldValue);
        }

        @Override
        public @Column.Type
        String getColumnType() {
            return Column.TEXT;
        }
    };

    T cursorToFieldValue(@NonNull CursorWrapper cursor, @NonNull String fieldName) throws IllegalAccessException;

    void fieldValueToContentValues(@NonNull ContentValuesWrapper contentValues, String fieldName, @NonNull T fieldValue);

    @Column.Type
    String getColumnType();
}

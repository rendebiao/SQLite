package com.rdb.sqlite;

import com.rdb.sqlite.annotation.EntityClass;
import com.rdb.sqlite.annotation.EntityColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EntityClass
public class Support {

    @EntityColumn(primary = true)
    int id;
    byte fbyte = 1;
    short fshort = 2;
    int fint = 3;
    long flong = 4;
    float ffloat = 5;
    double fdouble = 6;
    char fchar = 7;
    boolean fboolean = true;
    Byte fByte = 2;
    Short fShort = 3;
    Integer fInteger = 4;
    Long fLong = 5L;
    Float fFloat = 6f;
    Double fDouble = 7d;
    Character fCharacter = 8;
    Boolean fBoolean = true;
    byte[] fbyteArray = new byte[]{1, 2, 3};
    short[] fshortArray = new short[]{1, 2, 3};
    int[] fintArray = new int[]{1, 2, 3};
    long[] flongArray = new long[]{1, 2, 3};
    float[] ffloatArray = new float[]{1, 2, 3};
    double[] fdoubleArray = new double[]{1, 2, 3};
    char[] fcharArray = new char[]{1, 2, 3};
    boolean[] fbooleanArray = new boolean[]{true, false, true};
    Byte[] fByteArray = new Byte[]{1, 2, 3};
    Short[] fShortArray = new Short[]{1, 2, 3};
    Integer[] fIntegerArray = new Integer[]{1, 2, 3};
    Long[] fLongArray = new Long[]{1L, 2L, 3L};
    Float[] fFloatArray = new Float[]{1f, 2f, 3f};
    Double[] fDoubleArray = new Double[]{1d, 2d, 3d};
    Character[] fCharacterArray = new Character[]{1, 2, 3};
    Boolean[] fBooleanArray = new Boolean[]{true, false, true};
    List<Byte> fByteList = new ArrayList<>();
    List<Short> fShortList = new ArrayList<>();
    List<Integer> fIntegerList = new ArrayList<>();
    List<Long> fLongList = new ArrayList<>();
    List<Float> fFloatList = new ArrayList<>();
    List<Double> fDoubleList = new ArrayList<>();
    List<Character> fCharacterList = new ArrayList<>();
    List<Boolean> fBooleanList = new ArrayList<>();
    Map<String, Byte> fByteMap = new HashMap<>();
    Map<String, Short> fShortMap = new HashMap<>();
    Map<String, Integer> fIntegerMap = new HashMap<>();
    Map<String, Long> fLongMap = new HashMap<>();
    Map<String, Float> fFloatMap = new HashMap<>();
    Map<String, Double> fDoubleMap = new HashMap<>();
    Map<String, Character> fCharacterMap = new HashMap<>();
    Map<String, Boolean> fBooleanMap = new HashMap<>();

    public Support() {
        fByteList.add(Byte.valueOf((byte) 1));
        fByteList.add(Byte.valueOf((byte) 2));
        fShortList.add(Short.valueOf((short) 3));
        fShortList.add(Short.valueOf((short) 4));
        fIntegerList.add(Integer.valueOf(5));
        fIntegerList.add(Integer.valueOf(6));
        fLongList.add(Long.valueOf(7));
        fLongList.add(Long.valueOf(8));
        fFloatList.add(Float.valueOf(9));
        fFloatList.add(Float.valueOf(10));
        fDoubleList.add(Double.valueOf(11));
        fDoubleList.add(Double.valueOf(12));
        fCharacterList.add(Character.valueOf((char) 13));
        fCharacterList.add(Character.valueOf((char) 14));
        fBooleanList.add(Boolean.FALSE);
        fBooleanList.add(Boolean.TRUE);

        fByteMap.put("fByteMap", (byte) 1);
        fShortMap.put("fByteMap", (short) 1);
        fIntegerMap.put("fByteMap", 1);
        fLongMap.put("fByteMap", 1L);
        fFloatMap.put("fByteMap", 1F);
        fDoubleMap.put("fByteMap", 1d);
        fCharacterMap.put("fByteMap", (char) 1);
        fBooleanMap.put("fByteMap", Boolean.TRUE);
    }
}

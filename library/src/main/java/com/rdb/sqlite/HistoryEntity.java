package com.rdb.sqlite;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

public class HistoryEntity {

    private final Map<Class, HistoryClass> historyClassMap = new HashMap<>();

    public HistoryClass newHistoryClass(Class entityClass) {
        HistoryClass historyClass = historyClassMap.get(entityClass);
        if (historyClass == null) {
            historyClass = new HistoryClass(entityClass);
            historyClassMap.put(entityClass, historyClass);
        }
        return historyClass;
    }

    public Class getHistoryClass(Class entityClass, int version) {
        HistoryClass historyClass = historyClassMap.get(entityClass);
        return historyClass == null ? null : historyClass.getClass(version);
    }

    public Class[] getHistoryClasses(Class entityClass) {
        HistoryClass historyClass = historyClassMap.get(entityClass);
        return historyClass == null ? null : historyClass.getClasses();
    }

    public static class HistoryClass {

        Class entityClass;
        SparseArray<Class> array = new SparseArray<>();

        HistoryClass(Class entityClass) {
            this.entityClass = entityClass;
        }

        public HistoryClass putClass(int version, Class cls) {
            array.put(version, cls);
            return this;
        }

        Class getClass(int version) {
            return array.get(version);
        }

        Class[] getClasses() {
            Class[] classes = new Class[array.size()];
            for (int i = 0; i < classes.length; i++) {
                classes[i] = array.valueAt(i);
            }
            return classes;
        }
    }
}

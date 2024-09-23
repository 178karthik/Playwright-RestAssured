package com.karthik178.apimanager.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaUtils {


    public static <K, V> Map<K, V> convertToHashMap(List<K> keys, List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("Lists must have the same size");
        }

        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
}

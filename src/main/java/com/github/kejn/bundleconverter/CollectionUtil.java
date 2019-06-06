package com.github.kejn.bundleconverter;

import java.util.Arrays;
import java.util.List;

class CollectionUtil {

    private CollectionUtil(){}

    public static <E> E getOrDefault(List<E> list, int index, E defaultElement) {
        if (list == null || index >= list.size() || index < 0) {
            return defaultElement;
        }
        return list.get(index);
    }

    public static <E> E getOrDefaultFromArray(E[] array, int index, E defaultElement) {
        if (array == null) {
            return defaultElement;
        }
        return getOrDefault(Arrays.asList(array), index, defaultElement);
    }
}

package com.github.peacetrue.spring.util;

import org.springframework.core.ResolvableType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * a util class for {@code GenericParameter}
 *
 * @author xiayx
 */
public abstract class GenericParameterUtils {

    /**
     * convert {@link Collection} to {@link Map},
     * use generic parameter of element as key,
     * the element as value
     *
     * @param elements elements need to convert
     * @param clazz    the class of element
     * @param index    the generic parameter index of element
     * @param <T>      the type of element
     * @return the converted {@link Map}
     */
    public static <T> Map<Class, T> map(List<T> elements, Class<T> clazz, int index) {
        List<T> reverseList = new ArrayList<>(elements);
        Collections.reverse(reverseList);
        Function<T, Class> getType = item -> ResolvableType.forClass(clazz, item.getClass()).resolveGeneric(index);
        return reverseList.stream().collect(Collectors.toMap(getType, Function.identity(), EnumUtils.throwingMerger(), LinkedHashMap::new));
    }

    /**
     * similar to {@link #map(List, Class, int)},
     * default {@code index} to 0
     *
     * @param elements elements need to convert
     * @param clazz    the class of element
     * @param <T>      the type of element
     * @return the converted {@link Map}
     */
    public static <T> Map<Class, T> map(List<T> elements, Class<T> clazz) {
        return map(elements, clazz, 0);
    }

    /**
     * find the matched value from {@link Map}
     *
     * @param map         a map which the type of key is {@link Class}
     * @param targetClass the target class look in {@code map}
     * @param <T>         the type of {@link Map}' value
     * @return the matched value
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> find(Map<Class, T> map, Class targetClass) {
        //TODO consider the class priority, eg. Integer > Number > Comparable.
        List<Map.Entry<Class, T>> list = findList(map, targetClass);
        if (list.isEmpty()) return Optional.empty();
        list.sort((o1, o2) -> o1.getKey().isAssignableFrom(o2.getKey()) ? 1 : -1);
        return Optional.of(list.get(0).getValue());
    }

    private static <T> List<Map.Entry<Class, T>> findList(Map<Class, T> map, Class targetClass) {
        return map.entrySet().stream()
                .filter(entry -> ((Class<?>) entry.getKey()).isAssignableFrom(targetClass))
                .collect(Collectors.toList());
    }
}

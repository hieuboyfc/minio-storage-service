package com.minio.storage.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MapperUtils {

    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);

        return Arrays.stream(src.getPropertyDescriptors())
                .map(java.beans.PropertyDescriptor::getName)
                .filter(name -> ObjectUtils.isEmpty(src.getPropertyValue(name)))
                .distinct()
                .toArray(String[]::new);
    }

    public static String[] getEqualProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper des = new BeanWrapperImpl(target);

        return Arrays.stream(src.getPropertyDescriptors())
                .filter(pd -> equal(src.getPropertyValue(pd.getName()), des.getPropertyValue(pd.getName())))
                .map(java.beans.PropertyDescriptor::getName)
                .distinct()
                .toArray(String[]::new);
    }

    private static boolean equal(Object source, Object target) {
        return Objects.equals(source, target);
    }

    public static void mapIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static void mapIgnoreEquals(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getEqualProperties(src, target));
    }

    public static <S, D> D copyWithoutAudit(final S source, D destination) {
        return copy(source, destination, "createBy", "createDate", "version", "id");
    }

    public static <S, D> D copy(final S source, D destination) {
        BeanUtils.copyProperties(source, destination);
        return destination;
    }

    public static <S, D> D copy(final S source, D destination, String... ignore) {
        BeanUtils.copyProperties(source, destination, ignore);
        return destination;
    }

    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public static <D, T> List<D> map(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }

    public static <D, T> Set<D> mapToSet(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toSet());
    }

    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    public static List<Map<String, Object>> underscoreToCamelcase(List<Map<String, Object>> list, String... ignore) {
        List<Map<String, Object>> newList = new ArrayList<>();
        list.forEach(item -> {
            newList.add(underscoreToCamelcase(item, ignore));
        });
        return newList;
    }

    public static Map<String, Object> underscoreToCamelcase(Map<String, Object> map, String... ignore) {
        List<String> keyIgnores = Arrays.asList(ignore);
        Map<String, Object> newMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (keyIgnores.contains(key)) {
                newMap.put(key, value);
            } else {
                String newKey = Pattern.compile("_([a-z])").matcher(key.toLowerCase()).replaceAll(m -> m.group(1).toUpperCase());
                newMap.put(newKey, value);
            }
        });
        return newMap;
    }

}

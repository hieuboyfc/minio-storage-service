package com.minio.storage.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FileRequestUtils {

    public static <T, R> R findFirstField(List<T> requests,
                                          Function<T, Boolean> filterPredicate,
                                          Function<T, R> mapFunction,
                                          R defaultValue) {
        Optional<R> result = requests.stream()
                .filter(request -> ObjectUtils.isNotEmpty(filterPredicate.apply(request)))
                .map(mapFunction)
                .findFirst();
        return result.orElse(defaultValue);
    }

}

package com.minio.storage.utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class FindOptionalUtils {

    public static <T, R> R findFirstField(List<T> requests,
                                          Predicate<T> filterPredicate,
                                          Function<T, R> mapFunction,
                                          R defaultValue) {
        Optional<R> result = requests.stream()
                .filter(filterPredicate)
                .map(mapFunction)
                .filter(Objects::nonNull)
                .findFirst();
        return result.orElse(defaultValue);
    }

}

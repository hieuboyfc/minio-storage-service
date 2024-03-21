package com.minio.storage.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class StringGeneratorUtils {

    private static final int LENGTH=18;

    private StringGeneratorUtils() {
        throw new IllegalStateException();
    }

    public static String getRandomString(int count) {
        return RandomStringUtils.random(count, true, true);
    }

    public static String getRandomString() {
        return RandomStringUtils.random(LENGTH, true, true);
    }

    public static String getRandomPassword() {
        return UUID.randomUUID().toString();
    }

    public static String getLetterString(int count) {
        return RandomStringUtils.random(count, true, false);
    }

    public static String getLetterString() {
        return RandomStringUtils.random(LENGTH, true, false);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}

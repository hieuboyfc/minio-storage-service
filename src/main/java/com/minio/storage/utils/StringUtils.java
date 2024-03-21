package com.minio.storage.utils;

import com.minio.storage.utils.constant.Constants;

public class StringUtils {

    public static String concatCateNate(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : strings) {
            stringBuilder.append(value);
        }
        return stringBuilder.toString();
    }

    public static String getPathByType(String path, String type) {
        if (path.startsWith("/")) {
            return concatCateNate(type, path);
        } else {
            return concatCateNate(type, "/", path);
        }
    }

    public static String getCompanyPath(String path) {
        return getPathByType(path, Constants.COMPANY_FOLDER);
    }

    public static String getPrivatePath(String path) {
        return getPathByType(path, Constants.PRIVATE_FOLDER);
    }

    public static String getUserPath(String path) {
        return getPathByType(path, Constants.USER_FOLDER);
    }

}

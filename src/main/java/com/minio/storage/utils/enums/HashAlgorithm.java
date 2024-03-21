package com.minio.storage.utils.enums;

import java.util.HashMap;
import java.util.Map;

public enum HashAlgorithm {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512"),
    RIPEMD128("RIPEMD128"),
    RIPEMD160("RIPEMD160"),
    RIPEMD256("RIPEMD256"),
    GOST3411("GOST3411");

    private static final Map<String, HashAlgorithm> algorithmMap = new HashMap<>();
    private final String algorithm;

    static {
        for (HashAlgorithm alg : HashAlgorithm.values()) {
            algorithmMap.put(alg.getAlgorithmName(), alg);
        }
    }

    HashAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithmName() {
        return algorithm;
    }

    public static HashAlgorithm valueByKey(String key) {
        return algorithmMap.get(key);
    }

    }
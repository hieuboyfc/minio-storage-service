package com.minio.storage.utils.file;

import com.minio.storage.utils.constant.Constants;
import com.minio.storage.utils.enums.HashAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class FileChecksumUtils {

    public static String getFileChecksum(InputStream file) throws IOException, NoSuchAlgorithmException {
        return getFileChecksum(null, file);
    }

    public static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
        return getFileChecksum(null, new FileInputStream(file));
    }

    public static String getFileChecksum(HashAlgorithm algorithm, InputStream file) throws IOException, NoSuchAlgorithmException {
        try (InputStream inputStream = file) {
            return bytesToHex(calculate(algorithm, inputStream));
        }
    }

    public static String getFileChecksum(HashAlgorithm algorithm, File file) throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return getFileChecksum(algorithm, fileInputStream);
        }
    }

    public static String getByteChecksum(byte[] bytes) throws NoSuchAlgorithmException {
        return bytesToHex(calculate(null, bytes));
    }

    public static String getByteChecksum(HashAlgorithm algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        return bytesToHex(calculate(algorithm, bytes));
    }

    public static byte[] calculate(HashAlgorithm algorithm, InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(
                algorithm == null
                        ? Constants.DEFAULT_HASH_ALGORITHM.getAlgorithmName()
                        : algorithm.getAlgorithmName()
        );
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = inputStream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        return digest.digest();
    }

    public static byte[] calculate(HashAlgorithm algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(
                algorithm == null
                        ? Constants.DEFAULT_HASH_ALGORITHM.getAlgorithmName()
                        : algorithm.getAlgorithmName()
        );
        return digest.digest(bytes);
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(hash.length * 2);
        try (Formatter formatter = new Formatter(hexString)) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
        }
        return hexString.toString();
    }

}
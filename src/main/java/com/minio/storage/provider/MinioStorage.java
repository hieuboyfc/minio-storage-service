package com.minio.storage.provider;

import com.google.gson.JsonObject;
import com.minio.storage.process.MediaProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Log4j2
public class MinioStorage extends AbstractStorage {

    public MinioStorage(Environment env, MediaProcess mediaProcess, JsonObject config) throws IOException {
        super(env, mediaProcess, config);
    }
}

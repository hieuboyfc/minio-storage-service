package com.minio.storage.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvLoader {

    private final Dotenv dotenv;

    public EnvLoader() {
        this.dotenv = Dotenv.configure().load();
    }

    public String getPropertyFromEnv(String key) {
        return dotenv.get(key);
    }

}

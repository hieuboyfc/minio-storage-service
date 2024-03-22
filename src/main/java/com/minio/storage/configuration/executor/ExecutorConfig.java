package com.minio.storage.configuration.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5); // Số lượng luồng cố định
        taskExecutor.setMaxPoolSize(50); // Số lượng luồng tối đa
        taskExecutor.setQueueCapacity(100); // Số lượng tác vụ được chờ trong hàng đợi
        taskExecutor.setThreadNamePrefix("my-executor-"); // Tiền tố tên của các luồng
        taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

}


package com.taarifu_engine_api.modules.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

/**
 * Configuration class for asynchronous processing.
 * Enables async processing for email events and other notification tasks.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Creates a thread pool executor for email processing.
     * This executor handles asynchronous email sending tasks.
     *
     * @return configured thread pool executor
     */
    @Bean(name = "taskExecutor")
    @Primary
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size - minimum number of threads to keep alive
        executor.setCorePoolSize(2);
        
        // Maximum pool size - maximum number of threads to create
        executor.setMaxPoolSize(10);
        
        // Queue capacity - maximum number of tasks to queue
        executor.setQueueCapacity(100);
        
        // Thread name prefix for easier debugging
        executor.setThreadNamePrefix("EmailAsync-");
        
        // Keep alive time for idle threads (in seconds)
        executor.setKeepAliveSeconds(60);
        
        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Maximum time to wait for tasks to complete on shutdown (in seconds)
        executor.setAwaitTerminationSeconds(30);
        
        // Rejection policy - run rejected tasks in the calling thread
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // Initialize the executor
        executor.initialize();
        
        log.info("Email task executor configured with core pool size: {}, max pool size: {}, queue capacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }

    /**
     * Creates a thread pool executor for general notification processing.
     * This executor handles other notification tasks like SMS, push notifications, etc.
     *
     * @return configured thread pool executor
     */
    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("NotificationAsync-");
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        
        log.info("Notification task executor configured with core pool size: {}, max pool size: {}, queue capacity: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }

    /**
     * Creates a RestTemplate bean for HTTP client operations.
     * Used by SMS provider and other HTTP-based services.
     *
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

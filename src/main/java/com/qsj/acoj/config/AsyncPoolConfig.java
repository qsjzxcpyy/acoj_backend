package com.qsj.acoj.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;



@Slf4j
@EnableAsync
@Configuration

public class AsyncPoolConfig implements AsyncConfigurer{

        /**
         * 核心线程池大小
         */
        private static final int CORE_POOL_SIZE = 10;

        /**
         * 最大可创建的线程数
         */
        private static final int MAX_POOL_SIZE = 15;

        /**
         * 队列最大长度
         */
        private static final int QUEUE_CAPACITY = 1000;

        /**
         * 线程池维护线程所允许的空闲时间
         */
        private static final int KEEP_ALIVE_SECONDS = 300;

        // 创建线程池
        @Bean(name = "threadPoolTaskExecutor")
        public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setMaxPoolSize(MAX_POOL_SIZE);
            executor.setCorePoolSize(CORE_POOL_SIZE);
            executor.setQueueCapacity(QUEUE_CAPACITY);
            executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
            // 线程池对拒绝任务(无线程可用)的处理策略
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            return executor;
        }
        @Override
        public Executor getAsyncExecutor() {
            return threadPoolTaskExecutor();
        }

        // 异常处理
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new AsyncExceptionHandler();
        }

        @SuppressWarnings("all")
        class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
            @Override
            public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
                throwable.printStackTrace();
            }
        }


}

package priv.dawn.workers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExecutorConfiguration{

    @Bean("workerReadThreadPool")
    public ThreadPoolTaskExecutor getReadExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60 * 15);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); // 拒绝并且抛出异常
        executor.setWaitForTasksToCompleteOnShutdown(true); // 优雅销毁
        executor.setAwaitTerminationSeconds(60 * 10);
        executor.setThreadNamePrefix("WorkerAsyncThread-");
        executor.initialize(); //如果不初始化，导致找到不到执行器
        return executor;

    }
}

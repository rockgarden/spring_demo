package com.example.asyncmethod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * The @EnableAsync annotation switches on Spring’s ability to run @Async
 * methods in a background thread pool.
 * 
 * 通过@EnableAsync注释打开Spring在后台线程池中运行@Async方法的功能。
 * 
 * This class also customizes the Executor by defining a new bean. Here, the
 * method is named taskExecutor, since this is the specific method name for
 * which Spring searches. If you do not define an Executor bean, Spring creates
 * a SimpleAsyncTaskExecutor and uses that.
 * 
 * 此类还通过定义新bean来自定义Executor。在这里，该方法名为taskExecutor，因为这是Spring搜索的特定方法名称。
 * 如果没有定义Executor bean，Spring将创建SimpleAsyncTaskExecutor并使用它。
 */
@SpringBootApplication
@EnableAsync
public class AsyncMethodApplication {

	public static void main(String[] args) {
		// close the application context to shut down the custom ExecutorService
		SpringApplication.run(AsyncMethodApplication.class, args).close();
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		// the number of concurrent threads
		executor.setMaxPoolSize(2);
		// limit the size of the queue
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}

}

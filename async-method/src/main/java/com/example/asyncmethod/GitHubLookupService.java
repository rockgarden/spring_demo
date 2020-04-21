package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * The class uses Spring’s RestTemplate to invoke a remote REST point
 * (api.github.com/users/) and then convert the answer into a User object.
 * 
 * 本类使用Spring的RestTemplate调用远程REST点（api.github.com/users/），然后将答案转换为User对象。
 * 
 * Spring Boot automatically provides a RestTemplateBuilder that customizes the
 * defaults with any auto-configuration bits (that is, MessageConverter).
 * 
 * Spring Boot自动提供一个RestTemplateBuilder，它可以使用任何自动配置位（即MessageConverter）自定义默认值。
 * 
 * The class is marked with the @Service annotation, making it a candidate for
 * Spring’s component scanning to detect and add to the application context.
 * 
 * 该类标记有@Service批注，使其成为Spring组件扫描以检测并添加到应用程序上下文的候选对象。
 */
@Service
public class GitHubLookupService {

	private static final Logger logger = LoggerFactory.getLogger(GitHubLookupService.class);

	private final RestTemplate restTemplate;

	public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	/**
	 * The findUser method is flagged with Spring’s @Async annotation, indicating
	 * that it should run on a separate thread. The method’s return type is
	 * CompletableFuture<User> instead of User, a requirement for any asynchronous
	 * service.
	 * 
	 * 使用Spring的@Async注释标记findUser方法，表明该方法应在单独的线程上运行。
	 * 该方法的返回类型为CompletableFuture<User>而不是User，这是所有异步服务的要求。
	 * 
	 * This code uses the completedFuture method to return a CompletableFuture
	 * instance that is already completed with result of the GitHub query.
	 * 
	 * 这段代码使用completedFuture方法来返回CompletableFuture实例，该实例已经完成了GitHub查询的结果。
	 * 
	 * Creating a local instance of the GitHubLookupService class does NOT allow the
	 * findUser method to run asynchronously. It must be created inside
	 * a @Configuration class or picked up by @ComponentScan.
	 * 
	 * 创建GitHubLookupService类的本地实例不允许findUser方法异步运行。
	 * 它必须在@Configuration类内部创建或由@ComponentScan拾取。
	 * 
	 * @param user
	 * @return
	 * @throws InterruptedException
	 */
	@Async
	public CompletableFuture<User> findUser(String user) throws InterruptedException {
		logger.info("Looking up " + user);
		String url = String.format("https://api.github.com/users/%s", user);
		User results = restTemplate.getForObject(url, User.class);
		// Artificial delay of 1s for demonstration purposes
		Thread.sleep(1000L);
		return CompletableFuture.completedFuture(results);
	}

}

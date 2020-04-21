package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.AvailabilityFilteringRule;

@Configuration
public class SayHelloConfiguration {

  @Autowired
  IClientConfig ribbonClientConfig;

  /**
   * override the IPing used by the default load balancer. The default IPing is a
   * NoOpPing (which doesn’t actually ping server instances, instead always
   * reporting that they’re stable)
   * 
   * The PingUrl, which will ping a URL to check the status of each server. Say
   * Hello has, as you’ll recall, a method mapped to the / path; that means that
   * Ribbon will get an HTTP 200 response when it pings a running Say Hello
   * server.
   * 
   * IPing PingUrl try to resolve if the servers are lived, for all the servers
   * that you indicate in the property listOfServers, so you must start all the
   * services or remove the services that you don't need.
   * 
   * Error: java.net.ConnectException: Connection refused (Connection refused) at
   * java.net.ConnectException: Connection refused (Connection refused)
   * 
   * @param config
   * @return
   */
  @Bean
  public IPing ribbonPing(IClientConfig config) {
    return new PingUrl();
  }

  /**
   * override the IRule used by the default load balancer.
   * 
   * The default IRule is a ZoneAvoidanceRule (which avoids the Amazon EC2 zone
   * that has the most malfunctioning servers, and might thus be a bit difficult
   * to try out in our local environment).
   * 
   * the AvailabilityFilteringRule, will use Ribbon’s built-in circuit breaker
   * functionality to filter out any servers in an “open-circuit” state: if a ping
   * fails to connect to a given server, or if it gets a read failure for the
   * server, Ribbon will consider that server “dead” until it begins to respond
   * normally.
   * 
   * @param config
   * @return
   */
  @Bean
  public IRule ribbonRule(IClientConfig config) {
    return new AvailabilityFilteringRule();
  }

}

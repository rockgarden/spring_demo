package com.example.routingandfilteringgateway.filters.pre;

import javax.servlet.http.HttpServletRequest;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

  /**
   * Returns a `String` that stands for the type of the filter -- in this case,
   * `pre`. (It would be `route` for a routing filter.)
   */
  @Override
  public String filterType() {
    return "pre";
  }

  /**
   * Gives the order in which this filter is to be run, relative to other filters.
   */
  @Override
  public int filterOrder() {
    return 1;
  }

  /**
   * Contains the logic that determines when to run this filter (this particular
   * filter is always run).
   */
  @Override
  public boolean shouldFilter() {
    return true;
  }

  /**
   * Contains the functionality of the filter.
   * 
   * Zuul filters store request and state information in (and share it by means
   * of) the `RequestContext`. You can use that to get at the `HttpServletRequest`
   * and then log the HTTP method and URL of the request before it is sent on its
   * way.
   */
  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();

    log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

    return null;
  }

}

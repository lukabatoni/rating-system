package com.lukaoniani.ratingsystems.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "startTime";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME, startTime);

    log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex
  ) throws Exception {
    long startTime = (Long) request.getAttribute(START_TIME);
    long duration = System.currentTimeMillis() - startTime;

    log.info("Completed request: {} {} | Status: {} | Duration: {} ms",
        request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
  }
}

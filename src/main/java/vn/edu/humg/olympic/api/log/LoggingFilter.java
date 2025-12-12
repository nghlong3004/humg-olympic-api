package vn.edu.humg.olympic.api.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Project: humg-olympic-api
 *
 * @author nghlong3004
 * @since 12/12/2025
 */
@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startTime = System.currentTimeMillis();

    filterChain.doFilter(request, response);

    long duration = System.currentTimeMillis() - startTime;
    log.info(
        "Request: {} {} - Duration: {}ms", request.getMethod(), request.getRequestURI(), duration);
  }
}

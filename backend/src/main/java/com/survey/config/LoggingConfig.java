package com.survey.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Configuration for comprehensive logging throughout the application.
 */
@Configuration
public class LoggingConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);
    
    /**
     * Request logging filter for detailed HTTP request/response logging.
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        filter.setAfterMessageSuffix("");
        return filter;
    }
    
    /**
     * Add request/response logging interceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor());
    }
    
    /**
     * Interceptor for logging request details and adding trace IDs.
     */
    public static class RequestLoggingInterceptor implements HandlerInterceptor {
        
        private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST_LOGGER");
        
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String traceId = UUID.randomUUID().toString();
            request.setAttribute("traceId", traceId);
            
            requestLogger.info("Incoming request - TraceId: {}, Method: {}, URI: {}, User-Agent: {}, IP: {}", 
                             traceId, request.getMethod(), request.getRequestURI(), 
                             request.getHeader("User-Agent"), getClientIpAddress(request));
            
            return true;
        }
        
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                  Object handler, Exception ex) {
            String traceId = (String) request.getAttribute("traceId");
            
            if (ex != null) {
                requestLogger.error("Request completed with error - TraceId: {}, Status: {}, Error: {}", 
                                  traceId, response.getStatus(), ex.getMessage(), ex);
            } else {
                requestLogger.info("Request completed - TraceId: {}, Status: {}", 
                                 traceId, response.getStatus());
            }
        }
        
        private String getClientIpAddress(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                return xForwardedFor.split(",")[0];
            }
            
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                return xRealIp;
            }
            
            return request.getRemoteAddr();
        }
    }
} 
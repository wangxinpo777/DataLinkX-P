package com.datalinkx.dataserver.config.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;

@Component
public class RedisVisitFilter implements Filter {

    private final String REDIS_KEY_PREFIX = "daily_visit:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String today = LocalDate.now().toString(); // yyyy-MM-dd
        String key = REDIS_KEY_PREFIX + today;

        redisTemplate.opsForValue().increment(key);
        // 可选设置过期时间（30天）
        redisTemplate.expire(key, Duration.ofDays(30));

        chain.doFilter(request, response);
    }
}

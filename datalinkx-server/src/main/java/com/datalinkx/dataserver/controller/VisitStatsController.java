package com.datalinkx.dataserver.controller;

import com.datalinkx.dataserver.bean.vo.VisitStatsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/monitor/visit")
public class VisitStatsController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 获取今天的访问量
    @GetMapping("/today")
    public String getTodayVisits() {
        String today = LocalDate.now().toString();
        String key = "daily_visit:" + today;
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? "0" : count;
    }

    // 获取所有日期的访问量列表
    @GetMapping("/all")
    public List<VisitStatsVo> getAllVisitStats() {
        Set<String> keys = redisTemplate.keys("daily_visit:*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return keys.stream()
                .map(key -> {
                    String count = redisTemplate.opsForValue().get(key);
                    String date = key.replace("daily_visit:", "");
                    return new VisitStatsVo(date, count == null ? "0" : count);
                })
                .sorted(Comparator.comparing(VisitStatsVo::getDate)) // 按日期排序
                .collect(Collectors.toList());
    }
}

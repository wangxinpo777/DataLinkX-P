package com.datalinkx.dataserver.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.common.utils.JsonUtils;
import com.datalinkx.dataserver.bean.vo.SystemMonitorVo;
import com.datalinkx.dataserver.monitor.SystemMonitor;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import com.datalinkx.deepseek.service.DeepSeekService;
import com.datalinkx.security.utils.RedisCache;
import com.datalinkx.stream.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private DeepSeekService deepSeekService;
    @Autowired
    private DistributedLock distributedLock;
    @Autowired
    private RedisCache redisCache;

    private static final String SYSTEM_ANALYSIS_LOCK = "system_analysis_lock";
    private static final String SYSTEM_ANALYSIS = "system_analysis";

    @GetMapping("/server/info")
    public WebResult<SystemMonitorVo> info() throws Exception {
        return WebResult.of(SystemMonitor.stat());
    }

    @GetMapping("/server/info/analysis")
    public WebResult<String> analysis(@RequestParam(required = false) Boolean refresh) {
        if (refresh != null && refresh) {
            redisCache.deleteObject(SYSTEM_ANALYSIS);
        } else {
            String cache = redisCache.getCacheObject(SYSTEM_ANALYSIS);
            if (cache != null) {
                return WebResult.of(cache);
            }
        }
        String result = "";
        try {
            distributedLock.lock(SYSTEM_ANALYSIS_LOCK, "lock", 120);
            DeepSeekResponse deepSeekResponse = deepSeekService.getSystemAnalysis("deepseek-chat", SystemMonitor.stat().toString());
            result = deepSeekResponse.getChoices().get(0).getMessage().getContent();
            //缓存30分钟
            redisCache.setCacheObject(SYSTEM_ANALYSIS, result, 30 * 60, TimeUnit.SECONDS);
            distributedLock.unlock(SYSTEM_ANALYSIS_LOCK, "lock");
        } catch (Exception e) {
            distributedLock.unlock(SYSTEM_ANALYSIS_LOCK, "lock");
        }
        return WebResult.of(result);
    }

    @GetMapping("/city/mock")
    public WebResult<Object> mock() throws Exception {
        Resource resource = new DefaultResourceLoader().getResource("city_info.json");

        InputStream inputStream = resource.getInputStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return WebResult.of(JsonUtils.toJsonNode(content.toString()));
        }
    }
}

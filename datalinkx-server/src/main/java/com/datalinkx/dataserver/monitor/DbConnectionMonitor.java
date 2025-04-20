package com.datalinkx.dataserver.monitor;

import com.datalinkx.dataserver.repository.DsRepository;
import com.datalinkx.dataserver.service.impl.DsServiceImpl;
import com.datalinkx.security.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class DbConnectionMonitor {

    @Autowired
    private DsServiceImpl dsServiceImpl;
    @Autowired
    private DsRepository dsRepository;
    @Autowired
    private RedisCache redisCache;

    public static final String DB_CONNECTION_STATUS = "db_connection_status";

    // ✅ 启动时执行一次
    @PostConstruct
    public void init() {
        log.info("🔍 系统启动，执行一次数据库连接检测...");
        doCheckDbConnection();  // ✅ 安全调用
    }

    // ✅ 每10分钟检查一次连接状态
    @Scheduled(fixedRate = 600000)
    public void checkDbConnection() {
        doCheckDbConnection();  // ✅ 安全调用
    }

    // ✅ 真正执行检测逻辑的方法
    private void doCheckDbConnection() {
        dsRepository.findAllByIsDel(0).forEach(ds -> {
            try {
                dsServiceImpl.checkConnect(ds);
                redisCache.setCacheObject(DB_CONNECTION_STATUS + ds.getDsId(), "SUCCESS");
                log.info("✅ 数据源 {} 连接成功", ds.getDsId());
            } catch (Exception e) {
                redisCache.setCacheObject(DB_CONNECTION_STATUS + ds.getDsId(), "ERROR");
                log.warn("❌ 数据源 {} 连接失败：{}", ds.getDsId(), e.getMessage());
            }
        });
    }
}

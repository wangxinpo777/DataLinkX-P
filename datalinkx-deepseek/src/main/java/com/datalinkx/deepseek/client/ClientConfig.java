package com.datalinkx.deepseek.client;

import com.datalinkx.dataclient.config.DatalinkXClientUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public DeepSeekClient deepSeekClient(DeepseekProperties clientProperties) {
        return DatalinkXClientUtils.createClient("deepseek", clientProperties.getDeepseek(), DeepSeekClient.class, null);
    }
}

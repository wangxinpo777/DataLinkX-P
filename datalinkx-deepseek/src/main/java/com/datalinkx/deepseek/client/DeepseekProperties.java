package com.datalinkx.deepseek.client;

import com.datalinkx.dataclient.config.ClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "client")
@Data
public class DeepseekProperties {
    private ClientConfig.ServicePropertieBean deepseek;
}

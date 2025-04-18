package com.modelling.guiservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    private String host;
    private int port;
}

package com.modelling.guiservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageConfig {
    private String type;
    private LocalStorageConfig local;
    private S3StorageConfig s3;

    @Getter
    @Setter
    public static class LocalStorageConfig {
        private String path;
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class S3StorageConfig {
        private String bucketName;
        private String region;
    }
}
package com.modelling.guiservice.config;

import com.modelling.guiservice.config.properties.StorageConfig;
import com.modelling.guiservice.service.FileStorageService;
import com.modelling.guiservice.service.impl.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileStorageConfig {

    private final StorageConfig storageConfig;

//    @Bean
//    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
//    public FileStorageService s3FileStorageService() {
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                .withRegion(storageConfig.getS3().getRegion())
//                .build();
//        return new S3FileStorageService(
//                s3Client,
//                storageConfig.getS3().getBucketName()
//        );
//    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public FileStorageService localFileStorageService() {
        return new LocalFileStorageService(
                storageConfig.getLocal().getPath(),
                storageConfig.getLocal().getBaseUrl()
        );
    }
}

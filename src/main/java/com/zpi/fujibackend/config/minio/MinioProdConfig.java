package com.zpi.fujibackend.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class MinioProdConfig {

    @Value("${MINIO_URL}")
    private String minioUrl;

    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MINIO_SECRET_KEY}")
    private String secretKey;

    @Value("${MINIO_REGION}")
    private String minioRegion;

    @Bean
    public MinioClient minioClient() {

        return MinioClient.builder()
                .endpoint(minioUrl)
                .region(minioRegion)
                .credentials(accessKey, secretKey)
                .build();
    }
}

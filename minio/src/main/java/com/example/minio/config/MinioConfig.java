package com.example.minio.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wengly
 * @date 2022-06-21 15:12:08
 */
@Configuration
public class MinioConfig {

    @Value("${minio.ip}")
    private String ip;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
//        MinioClient minioClient = MinioClient.builder()
//                .endpoint(ip, port, false) //https or not
//                .credentials(accessKey, secretKey)
//                .build();
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ip) //https or not
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }
}

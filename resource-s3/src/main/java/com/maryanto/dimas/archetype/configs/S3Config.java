package com.maryanto.dimas.archetype.configs;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private final String endpointUrl;
    private final Integer endpointPort;
    private final Boolean endpointSecure;
    private final String accessKey;
    private final String secretKey;
    private final String regionName;
    private final Boolean regionEnabled;
    private final String bucketName;

    public S3Config(
            @Value("${minio.endpoint.url}") String endpointUrl,
            @Value("${minio.endpoint.port}") Integer endpointPort,
            @Value("${minio.endpoint.secure}") Boolean endpointSecure,
            @Value("${minio.region.name}") String regionName,
            @Value("${minio.region.enabled}") Boolean regionEnabled,
            @Value("${minio.credential.access_key}") String accessKey,
            @Value("${minio.credential.secret_key}") String secretKey,
            @Value("${minio.bucket.name}") String bucketName) {
        this.endpointUrl = endpointUrl;
        this.endpointPort = endpointPort;
        this.endpointSecure = endpointSecure;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.regionName = regionName;
        this.regionEnabled = regionEnabled;
        this.bucketName = bucketName;
    }

    @Bean(name = "bucketName")
    public String getBucketName() {
        return this.bucketName;
    }

    @Bean
    public MinioClient client() {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(endpointUrl, endpointPort, endpointSecure)
                .credentials(accessKey, secretKey)
                .region(this.regionName);

        return builder.build();
    }
}

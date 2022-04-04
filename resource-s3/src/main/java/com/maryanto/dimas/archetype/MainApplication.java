package com.maryanto.dimas.archetype;

import com.maryanto.dimas.archetype.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
@Slf4j
public class MainApplication implements ApplicationRunner {

    private MinioService service;

    public MainApplication(MinioService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!this.service.isBucketExists()) {
            log.warn("bucket not exists, we'll create for you!");
            this.service.createdBucket();
        }
    }
}

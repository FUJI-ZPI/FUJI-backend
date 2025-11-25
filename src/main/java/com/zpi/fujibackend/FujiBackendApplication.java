package com.zpi.fujibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
class FujiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FujiBackendApplication.class, args);
    }

}

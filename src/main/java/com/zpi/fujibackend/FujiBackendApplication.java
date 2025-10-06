package com.zpi.fujibackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class FujiBackendApplication {

    public static void main(String[] args) {
        Dotenv.configure().systemProperties().load();
        SpringApplication.run(FujiBackendApplication.class, args);
    }

}

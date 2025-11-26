package com.zpi.fujibackend.config.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${app.firebase-config-file}")
    private Resource firebaseConfigFile;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream is = firebaseConfigFile.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .build();
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized");
                return app;
            }
        }
        log.info("Firebase already initialized");
        return FirebaseApp.getInstance();
    }
}

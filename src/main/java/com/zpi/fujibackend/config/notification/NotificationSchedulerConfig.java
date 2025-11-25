package com.zpi.fujibackend.config.notification;

import com.zpi.fujibackend.notification.NotificationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationSchedulerConfig {

    private final NotificationFacade notificationFacade;

    private static final String NOTIFICATION_HOURS = "0 0 18,22 * * *";

    @Scheduled(cron = NOTIFICATION_HOURS)
    @Transactional
    public void sendDailyNotification() {
        notificationFacade.sendDailyNotificationToAllUsers();
    }


}
package com.zpi.fujibackend.notification.domain;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.zpi.fujibackend.notification.NotificationFacade;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
class NotificationService implements NotificationFacade {

    private final UserFacade userFacade;

    @Override
    public void sendNotificationToCurrentUser(String title, String body) {
        User user = userFacade.getCurrentUser();
        String token = user.getFcmToken();

        if (token == null || token.isBlank()) {
            log.info("Notification not sent: FC token null/blank for user {}", user.getId());
            return;
        }
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending FCM message", e);
        }
    }
}

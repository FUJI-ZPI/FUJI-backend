package com.zpi.fujibackend.notification;

public interface NotificationFacade {

    void sendNotificationToCurrentUser(String title, String body);

    void sendDailyNotificationToAllUsers();


}

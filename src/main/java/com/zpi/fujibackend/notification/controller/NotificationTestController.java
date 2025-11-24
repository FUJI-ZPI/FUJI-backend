package com.zpi.fujibackend.notification.controller;

import com.zpi.fujibackend.notification.NotificationFacade;
import com.zpi.fujibackend.notification.dto.TestNotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-test")
@RequiredArgsConstructor
class NotificationTestController {

    private final NotificationFacade notificationFacade;


    @PostMapping("/send-current-user")
    public String sendTestNotificationCurrent(@RequestBody @Valid TestNotificationRequest request) {
        notificationFacade.sendNotificationToCurrentUser(request.title(), request.body());
        return "Success [Current] :)";
    }

    @PostMapping("/send-all")
    public String sendTestNotificationAll() {
        notificationFacade.sendDailyNotificationToAllUsers();
        return "Success [All] :)";
    }

}
package com.zpi.fujibackend.user;

import com.zpi.fujibackend.user.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserFacade {
    UUID findOrCreateByEmail(final String email);

    User getCurrentUser();

    Long getCurrentUserId();

    void setCurrentUserFcmToken(final String fcmToken);

    List<User> findAllUsersByFcmTokenIsNotNull();
}

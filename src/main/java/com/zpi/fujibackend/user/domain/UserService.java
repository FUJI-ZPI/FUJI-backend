package com.zpi.fujibackend.user.domain;

import com.zpi.fujibackend.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
class UserService implements UserFacade {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UUID findOrCreateByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email))).getUuid();
    }

    @Override
    public User getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userUuid = (String) auth.getPrincipal();
        return getUserByUuid(UUID.fromString(userUuid));
    }

    @Override
    public void increaseUserLevel() {
        User currentUser = getCurrentUser();
        currentUser.setLevel(currentUser.getLevel() + 1);
        userRepository.save(currentUser);
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public Integer getCurrentUserLevel() {
        return getCurrentUser().getLevel();
    }

    @Override
    @Transactional
    public void setCurrentUserFcmToken(String fcmToken) {
        userRepository.updateFcmTokenById(getCurrentUserId(), fcmToken);
    }

    private User getUserByUuid(final UUID uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(
                () -> new RuntimeException("User with uuid " + uuid + " not found")
        );
    }
}

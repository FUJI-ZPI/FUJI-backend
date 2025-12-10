package com.zpi.fujibackend.user.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findOrCreateByEmail_ShouldReturnExistingUuid_WhenUserExists() {
        String email = "existing@example.com";
        UUID existingUuid = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setUuid(existingUuid);
        existingUser.setEmail(email);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        UUID result = userService.findOrCreateByEmail(email);

        assertThat(result).isEqualTo(existingUuid);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreateByEmail_ShouldCreateNewUserAndReturnUuid_WhenUserDoesNotExist() {
        String email = "new@example.com";
        UUID newUuid = UUID.randomUUID();
        User savedUser = new User();
        savedUser.setUuid(newUuid);
        savedUser.setEmail(email);

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        UUID result = userService.findOrCreateByEmail(email);

        assertThat(result).isEqualTo(newUuid);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(email);
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenContextHasValidUuid() {
        UUID userUuid = UUID.randomUUID();
        String uuidString = userUuid.toString();
        User expectedUser = new User();
        expectedUser.setUuid(userUuid);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(uuidString);
        given(userRepository.findByUuid(userUuid)).willReturn(Optional.of(expectedUser));

        try (MockedStatic<SecurityContextHolder> mockedSecurity = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User result = userService.getCurrentUser();

            assertThat(result).isEqualTo(expectedUser);
        }
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenUserNotFoundInDb() {
        UUID userUuid = UUID.randomUUID();
        String uuidString = userUuid.toString();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(uuidString);
        given(userRepository.findByUuid(userUuid)).willReturn(Optional.empty());

        try (MockedStatic<SecurityContextHolder> mockedSecurity = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User with uuid " + userUuid + " not found");
        }
    }

    @Test
    void getCurrentUserId_ShouldReturnId() {
        UUID userUuid = UUID.randomUUID();
        String uuidString = userUuid.toString();
        Long userId = 123L;
        User user = new User();
        user.setUuid(userUuid);
        user.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(uuidString);
        given(userRepository.findByUuid(userUuid)).willReturn(Optional.of(user));

        try (MockedStatic<SecurityContextHolder> mockedSecurity = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Long result = userService.getCurrentUserId();

            assertThat(result).isEqualTo(userId);
        }
    }

    @Test
    void setCurrentUserFcmToken_ShouldUpdateTokenForCurrentUser() {
        UUID userUuid = UUID.randomUUID();
        String uuidString = userUuid.toString();
        Long userId = 999L;
        String fcmToken = "token_abc_123";

        User user = new User();
        user.setUuid(userUuid);
        user.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(uuidString);
        given(userRepository.findByUuid(userUuid)).willReturn(Optional.of(user));

        try (MockedStatic<SecurityContextHolder> mockedSecurity = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            userService.setCurrentUserFcmToken(fcmToken);

            verify(userRepository).updateFcmTokenById(userId, fcmToken);
        }
    }

    @Test
    void findAllUsersByFcmTokenIsNotNull_ShouldReturnList() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = List.of(user1, user2);

        given(userRepository.findAllByFcmTokenIsNotNull()).willReturn(users);

        List<User> result = userService.findAllUsersByFcmTokenIsNotNull();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(user1, user2);
    }

    @Test
    void findAllUsersByFcmTokenIsNotNull_ShouldReturnEmptyList() {
        given(userRepository.findAllByFcmTokenIsNotNull()).willReturn(Collections.emptyList());

        List<User> result = userService.findAllUsersByFcmTokenIsNotNull();

        assertThat(result).isEmpty();
    }
}
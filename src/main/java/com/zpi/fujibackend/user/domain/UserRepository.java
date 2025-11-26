package com.zpi.fujibackend.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(final UUID uuid);

    Optional<User> findByEmail(final String email);

    @Modifying
    @Query("UPDATE User u SET u.fcmToken = :fcmToken WHERE u.id = :id")
    void updateFcmTokenById(@Param("id") final Long id, @Param("fcmToken") final String fcmToken);

    List<User> findAllByFcmTokenIsNotNull();


}

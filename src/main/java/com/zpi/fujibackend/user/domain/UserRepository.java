package com.zpi.fujibackend.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(final UUID uuid);

    Optional<User> findByEmail(final String email);
}

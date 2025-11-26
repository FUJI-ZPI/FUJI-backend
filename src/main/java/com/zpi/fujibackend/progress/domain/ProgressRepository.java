package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findProgressByUser(User user);
}

package com.zpi.fujibackend.srs.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.nextDue <= :now ORDER BY c.nextDue ASC")
    List<Card> findDueForUser(@Param("userId") Long userId, @Param("now") Instant now, Pageable pageable);

    Card findByUuid(UUID uuid);
}

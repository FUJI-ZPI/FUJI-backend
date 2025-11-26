package com.zpi.fujibackend.srs.domain;

import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.nextDue <= :now ORDER BY c.nextDue ASC")
    List<Card> findDueForUser(@Param("userId") Long userId, @Param("now") Instant now, Pageable pageable);

    Optional<Card> findByUserAndKanji(User user, Kanji kanji);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId AND c.familiarity = :maxFamiliarity")
    Integer countByUserAndMaxFamiliarity(@Param("userId") Long userId, @Param("maxFamiliarity") int maxFamiliarity);
}

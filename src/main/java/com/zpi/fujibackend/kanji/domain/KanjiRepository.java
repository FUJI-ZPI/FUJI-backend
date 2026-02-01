package com.zpi.fujibackend.kanji.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface KanjiRepository extends JpaRepository<Kanji, Long> {

    List<Kanji> findByLevel(int level);

    Optional<Kanji> getByUuid(UUID uuid);

    @Query("""
            SELECT k
            FROM Kanji k
            LEFT JOIN Card c ON c.kanji = k AND c.user.id = :userId
            WHERE c.id IS NULL
              AND k.level = :level
            """)
    List<Kanji> findAllNotInCardsForUser(@Param("userId") Long userId, @Param("level") int level, Pageable pageable);

    Optional<Kanji> findByUuid(UUID uuid);

    List<Kanji> findByDrawingDataCount(int strokeNumber);

    @Query("""
        SELECT COUNT(k)
        FROM Kanji k
        WHERE k.level <= :maxLevel
        AND k NOT IN (
            SELECT learned
            FROM Progress p
            JOIN p.learnedKanji learned
            WHERE p.user.id = :userId
        )
    """)
    long countMissingKanjiForUser(@Param("userId") Long userId, @Param("maxLevel") int maxLevel);
}

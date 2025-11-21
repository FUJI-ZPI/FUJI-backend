package com.zpi.fujibackend.activity.domain;

import com.zpi.fujibackend.activity.dto.ActivityPlaybackDetails;
import com.zpi.fujibackend.activity.dto.DailyActivityDetail;
import com.zpi.fujibackend.activity.dto.DailyActivityStat;
import com.zpi.fujibackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("""
        SELECT new com.zpi.fujibackend.activity.dto.DailyActivityStat(
            cast(a.timestamp as LocalDate),
            COUNT(a)
        )
        FROM Activity a
        WHERE a.card.user = :user
          AND a.timestamp >= :startDate
        GROUP BY cast(a.timestamp as LocalDate)
        ORDER BY cast(a.timestamp as LocalDate) ASC
    """)
    List<DailyActivityStat> findStatsByUser(@Param("user") User user, @Param("startDate") Instant startDate);

    @Query("""
        SELECT new com.zpi.fujibackend.activity.dto.DailyActivityDetail(
            a.uuid,
            a.timestamp,
            k.character,
            a.activityType,
            a.overallAccuracy
        )
        FROM Activity a
        JOIN a.card c
        JOIN c.kanji k
        WHERE c.user = :user
          AND a.timestamp >= :startOfDay
          AND a.timestamp < :endOfDay
        ORDER BY a.timestamp DESC
    """)
    List<DailyActivityDetail> findDetailsForDay(@Param("user") User user, @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    @Query("""
        SELECT new com.zpi.fujibackend.activity.dto.ActivityPlaybackDetails(
            k.character,
            a.drawingData,
            a.overallAccuracy,
            a.strokesAccuracy,
            k.drawingData
        )
        FROM Activity a
        JOIN a.card c
        JOIN c.kanji k
        WHERE a.uuid = :uuid
          AND c.user = :user
    """)
    Optional<ActivityPlaybackDetails> findPlaybackDetails(@Param("uuid") UUID uuid, @Param("user") User user);
}

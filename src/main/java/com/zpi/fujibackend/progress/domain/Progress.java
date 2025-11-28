package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "progress")
public class Progress extends AbstractUuidEntity {

    @OneToOne
    private User user;

    @Column(name = "user_level", nullable = false)
    private Integer level;

    @Column(name = "daily_streak", nullable = false)
    private Integer dailyStreak;

    @Column(name = "max_daily_streak", nullable = false)
    private Integer maxDailyStreak;

    @Column(name = "last_streak_updated")
    private Instant lastStreakUpdated;

    @ManyToMany
    @JoinTable(
            name = "user_learned_kanji",
            joinColumns = @JoinColumn(name = "progress_id"),
            inverseJoinColumns = @JoinColumn(name = "kanji_id")
    )
    private Set<Kanji> learnedKanji = new HashSet<>();

    public Progress(final User user) {
        this.user = user;
        this.level = 1;
        this.dailyStreak = 0;
        this.maxDailyStreak = 0;
        this.lastStreakUpdated = null;
    }
}

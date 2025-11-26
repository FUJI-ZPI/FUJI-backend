package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import com.zpi.fujibackend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_progress")
public class Progress extends AbstractUuidEntity {

    @OneToOne
    private User user;

    @Column(name = "user_level", nullable = false)
    private Integer level;

    @Column(name = "daily_streak", nullable = false)
    private Integer dailyStreak;

    @Column(name = "streak_updated")
    private Instant streakUpdated;

    public Progress(final User user) {
        this.user = user;
        this.level = 1;
        this.dailyStreak = 0;
        this.streakUpdated = null;
    }
}

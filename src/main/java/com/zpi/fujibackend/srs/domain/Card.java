package com.zpi.fujibackend.srs.domain;

import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards", schema = "wanikani")
public class Card extends AbstractUuidEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanji_id")
    private Kanji kanji;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "familiarity")
    private Integer familiarity;

    @Column(name = "interval_hours")
    private Integer intervalHours;

    @Column(name = "last_reviewed")
    private Instant lastReviewed;

    @Column(name = "next_due")
    private Instant nextDue;
}

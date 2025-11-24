package com.zpi.fujibackend.activity.domain;

import com.zpi.fujibackend.activity.dto.ActivityType;
import com.zpi.fujibackend.common.entity.AbstractUuidEntity;
import com.zpi.fujibackend.srs.domain.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_activity")
class Activity extends AbstractUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @Column(nullable = false)
    private Instant timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "drawing_data", columnDefinition = "jsonb", nullable = false)
    private List<List<List<Double>>> drawingData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "strokes_accuracy", columnDefinition = "jsonb", nullable = false)
    private List<Double> strokesAccuracy;

    @Column(name = "overall_accuracy", nullable = false)
    private double overallAccuracy;
}


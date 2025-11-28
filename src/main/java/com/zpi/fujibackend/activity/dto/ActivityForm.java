package com.zpi.fujibackend.activity.dto;

import com.zpi.fujibackend.srs.domain.Card;

import java.util.List;

public record ActivityForm(Card card,
                           ActivityType activityType,
                           List<List<List<Double>>> drawingData,
                           List<Double> strokesAccuracy,
                           double overallAccuracy,
                           boolean isSuccess) {
}

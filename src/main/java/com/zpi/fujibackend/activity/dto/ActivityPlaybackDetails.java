package com.zpi.fujibackend.activity.dto;

import java.util.List;

public record ActivityPlaybackDetails(String character,
                                      List<List<List<Double>>> userStrokes,
                                      double overallAccuracy,
                                      List<Double> strokesAccuracy,
                                      List<List<List<Double>>> referenceStrokes) {

    public ActivityPlaybackDetails withNormalizedReference(List<List<List<Double>>> normalizedReference) {
        return new ActivityPlaybackDetails(
                character,
                userStrokes,
                overallAccuracy,
                strokesAccuracy,
                normalizedReference
        );
    }

}
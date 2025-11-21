package com.zpi.fujibackend.algorithm;

import java.util.List;

public record AccuracyStrokeForm(List<List<Double>> userStroke,
                                 List<List<Double>> referenceStroke) {
}

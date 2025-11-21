package com.zpi.fujibackend.algorithm;

import java.util.List;

public record AccuracyForm(List<List<List<Double>>> userStrokes,
                           List<List<List<Double>>> referenceStrokes) {
}

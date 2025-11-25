package com.zpi.fujibackend.recognizer.domain;

import java.util.List;

record SortedPatternPair(List<List<List<Double>>> pattern1,
                         List<List<List<Double>>> pattern2,
                         int pattern1StrokeCount,
                         int pattern2StrokeCount
) {
}
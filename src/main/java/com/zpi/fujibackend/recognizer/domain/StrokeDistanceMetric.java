package com.zpi.fujibackend.recognizer.domain;

import java.util.List;

@FunctionalInterface
interface StrokeDistanceMetric {
    double apply(List<List<Double>> stroke1, List<List<Double>> stroke2);
}
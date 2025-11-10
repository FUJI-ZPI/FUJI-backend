package com.zpi.fujibackend.algorithm;

import java.util.ArrayList;
import java.util.List;

public class PatternFeatureExtractor {


    public static List<List<List<Double>>> extractFeatures(List<List<List<Double>>> pointStrokes, double interval) {

        List<List<List<Double>>> extractedPattern = new ArrayList<>();

        for (List<List<Double>> stroke : pointStrokes) {
            List<List<Double>> extractedStroke = new ArrayList<>();

            if (stroke.isEmpty()) {
                extractedPattern.add(extractedStroke);
                continue;
            }

            double accumulatedDistance = 0.0;

            extractedStroke.add(stroke.getFirst());

            for (int j = 1; j < stroke.size(); j++) {

                List<Double> previousPoint = stroke.get(j - 1);
                List<Double> currentPoint = stroke.get(j);

                accumulatedDistance += euclideanDistance(previousPoint, currentPoint);

                if (accumulatedDistance >= interval) {
                    accumulatedDistance -= interval;
                    extractedStroke.add(currentPoint);
                }
            }


            if (extractedStroke.size() == 1) {
                List<Double> endPoint = stroke.getLast();
                if (!pointsEqual(endPoint, extractedStroke.getFirst())) {
                    extractedStroke.add(endPoint);
                }
            } else {
                if (accumulatedDistance > 0.75 * interval) {

                    List<Double> endPoint = stroke.getLast();
                    if (!pointsEqual(endPoint, extractedStroke.getLast())) {
                        extractedStroke.add(endPoint);
                    }
                }
            }

            extractedPattern.add(extractedStroke);
        }

        return extractedPattern;
    }

    private static double euclideanDistance(List<Double> p1, List<Double> p2) {
        double dx = p1.get(0) - p2.get(0);
        double dy = p1.get(1) - p2.get(1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static boolean pointsEqual(List<Double> p1, List<Double> p2) {
        return p1.get(0).equals(p2.get(0)) && p1.get(1).equals(p2.get(1));
    }
}
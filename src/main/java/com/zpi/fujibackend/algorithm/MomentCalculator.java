package com.zpi.fujibackend.algorithm;

import java.util.List;

public class MomentCalculator {

    private static double getX(List<Double> point) {
        return point.getFirst();
    }

    private static double getY(List<Double> point) {
        return point.get(1);
    }


    public static double calculateMomentM10(List<List<List<Double>>> pointStrokes) {
        double m10 = 0.0;
        for (List<List<Double>> stroke : pointStrokes) {
            for (List<Double> point : stroke) {
                m10 += getX(point);
            }
        }
        return m10;
    }

    public static double calculateMomentM01(List<List<List<Double>>> pointStrokes) {
        double m01 = 0.0;
        for (List<List<Double>> stroke : pointStrokes) {
            for (List<Double> point : stroke) {
                m01 += getY(point);
            }
        }
        return m01;
    }

    public static double calculateTotalMassM00(List<List<List<Double>>> pointStrokes) {
        double m00 = 0.0;
        for (List<List<Double>> stroke : pointStrokes) {
            m00 += stroke.size();
        }
        return m00;
    }

    public static double calculateCentralMomentMu20(List<List<List<Double>>> pointStrokes, double centroidX) {
        double mu20 = 0.0;
        for (List<List<Double>> stroke : pointStrokes) {
            for (List<Double> point : stroke) {
                double dx = getX(point) - centroidX;
                mu20 += dx * dx;
            }
        }
        return mu20;
    }

    public static double calculateCentralMomentMu02(List<List<List<Double>>> pointStrokes, double centroidY) {
        double mu02 = 0.0;
        for (List<List<Double>> stroke : pointStrokes) {
            for (List<Double> point : stroke) {
                double dy = getY(point) - centroidY;
                mu02 += dy * dy;
            }
        }
        return mu02;
    }
}

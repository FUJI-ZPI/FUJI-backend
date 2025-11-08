package com.zpi.fujibackend.algorithm;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class KanjiNormalizer {

    private static final double DEFAULT_CANVAS_SIZE = 256.0;

    private static double getX(List<Double> p) {
        return p.getFirst();
    }

    private static double getY(List<Double> p) {
        return p.get(1);
    }

    private static List<Double> point(double x, double y) {
        return List.of(x, y);
    }

    public static List<List<List<Double>>> momentNormalizeFixed(final List<List<List<Double>>> kanji) {
        final List<List<List<Double>>> normalizedPattern = new ArrayList<>();

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;


        for (List<List<Double>> stroke : kanji) {
            for (List<Double> p : stroke) {
                double x = getX(p);
                double y = getY(p);

                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
        }

        double width = maxX - minX;
        double height = maxY - minY;

        if (width == 0 && height == 0) return kanji;

        double scale = Math.min(DEFAULT_CANVAS_SIZE / width, DEFAULT_CANVAS_SIZE / height);

        double scaledWidth = width * scale;
        double scaledHeight = height * scale;

        double padX = (DEFAULT_CANVAS_SIZE - scaledWidth) / 2.0;
        double padY = (DEFAULT_CANVAS_SIZE - scaledHeight) / 2.0;

        for (List<List<Double>> stroke : kanji) {
            List<List<Double>> newStroke = new ArrayList<>();
            for (List<Double> p : stroke) {

                double tx = (getX(p) - minX) * scale + padX;
                double ty = (getY(p) - minY) * scale + padY;

                newStroke.add(point(tx, ty));
            }
            normalizedPattern.add(newStroke);
        }

        return normalizedPattern;
    }


    public static List<List<List<Double>>> momentNormalize(final List<List<List<Double>>> kanji) {

        final List<List<List<Double>>> normalizedPattern = new ArrayList<>();

        double minX = DEFAULT_CANVAS_SIZE, maxX = 0;
        double minY = DEFAULT_CANVAS_SIZE, maxY = 0;

        for (List<List<Double>> stroke : kanji) {
            for (List<Double> p : stroke) {
                double x = getX(p), y = getY(p);
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
        }

        double width = abs(maxX - minX);
        double height = abs(maxY - minY);

        double r2 = calculateAspectRatioFactor(width, height);

        double normW = DEFAULT_CANVAS_SIZE;
        double normH = DEFAULT_CANVAS_SIZE;

        if (height > width) {
            normW = r2 * DEFAULT_CANVAS_SIZE;
        } else {
            normH = r2 * DEFAULT_CANVAS_SIZE;
        }

        double padX = (DEFAULT_CANVAS_SIZE - normW) / 2.0;
        double padY = (DEFAULT_CANVAS_SIZE - normH) / 2.0;

        double m00 = MomentCalculator.calculateTotalMassM00(kanji);
        double m01 = MomentCalculator.calculateMomentM01(kanji);
        double m10 = MomentCalculator.calculateMomentM10(kanji);

        double cx = m10 / m00;
        double cy = m01 / m00;

        double targetCx = normW / 2.0;
        double targetCy = normH / 2.0;

        double mu20 = MomentCalculator.calculateCentralMomentMu20(kanji, cx);
        double mu02 = MomentCalculator.calculateCentralMomentMu02(kanji, cy);

        double alpha = normW / (4.0 * sqrt(mu20 / m00));
        double beta = normH / (4.0 * sqrt(mu02 / m00));

        for (List<List<Double>> stroke : kanji) {
            List<List<Double>> newStroke = new ArrayList<>();
            for (List<Double> p : stroke) {

                double newX = alpha * (getX(p) - cx) + targetCx;
                double newY = beta * (getY(p) - cy) + targetCy;

                newStroke.add(point(newX, newY));
            }
            normalizedPattern.add(newStroke);
        }

        return applyTranslation(normalizedPattern, padX, padY);
    }


    private static double calculateAspectRatioFactor(double w, double h) {
        double r1 = (h > w) ? w / h : h / w;
        return sqrt(Math.sin((Math.PI / 2.0) * r1));
    }

    private static List<List<List<Double>>> applyTranslation(List<List<List<Double>>> pattern,
                                                             double dx, double dy) {

        List<List<List<Double>>> out = new ArrayList<>();

        for (List<List<Double>> stroke : pattern) {
            List<List<Double>> newStroke = new ArrayList<>();
            for (List<Double> p : stroke) {
                newStroke.add(point(getX(p) + dx, getY(p) + dy));
            }
            out.add(newStroke);
        }

        return out;
    }
}

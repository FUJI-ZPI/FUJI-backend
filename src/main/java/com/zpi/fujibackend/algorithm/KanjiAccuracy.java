package com.zpi.fujibackend.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;

public class KanjiAccuracy {

    public record KanjiAccuracyResult(double overallAccuracy, List<Double> strokeAccuracies) {
    }

    public static class KanjiComparator {

        private static final int RESAMPLE_POINTS_COUNT = 128;

        private static final double STROKE_TOLERANCE_POSITION = 50.0;
        private static final double MAX_ALLOWED_ANCHOR_ERROR = 1.0;

        private static final double DIRECTION_TOLERANCE_RADIANS = 1.3;
        private static final double TURNING_TOLERANCE_RADIANS = Math.PI / 4.0;

        private static final double WEIGHT_POS_CURVED = 0.4;
        private static final double WEIGHT_DIR_CURVED = 0.4;
        private static final double WEIGHT_TURN_CURVED = 0.2;

        private static final double WEIGHT_POS_STRAIGHT = 0.3;
        private static final double WEIGHT_DIR_STRAIGHT = 0.3;
        private static final double WEIGHT_TURN_STRAIGHT = 0.4;

        private static final double STRAIGHTNESS_THRESHOLD_RADIANS = Math.PI / 12.0;

        private static final double WEIGHT_HYBRID_DTW_SHAPE = 0.95;
        private static final double WEIGHT_HYBRID_LENGTH = 0.05;

        private static final double LENGTH_ERROR_TOLERANCE = 0.25;

        public static KanjiAccuracyResult calculateKanjiAccuracy(
                List<List<List<Double>>> userKanji,
                List<List<List<Double>>> referenceKanji
        ) {

            if (userKanji.size() != referenceKanji.size()) {
                return new KanjiAccuracyResult(0.0, Collections.emptyList());
            }
            if (referenceKanji.isEmpty()) return new KanjiAccuracyResult(1.0, Collections.emptyList());

            double totalScore = 0.0;
            List<Double> strokeScores = new ArrayList<>();

            for (int i = 0; i < referenceKanji.size(); i++) {
                List<List<Double>> userStroke = userKanji.get(i);
                List<List<Double>> refStroke = referenceKanji.get(i);

                double score;
                if (userStroke.size() < 2 || refStroke.size() < 2) {
                    score = 0.0;
                } else {
                    score = calculateStrokeScore(userStroke, refStroke);
                }

                totalScore += score;
                strokeScores.add(score);
            }

            return new KanjiAccuracyResult(totalScore / referenceKanji.size(), strokeScores);
        }

        private static double calculateStrokeScore(List<List<Double>> user, List<List<Double>> ref) {

            double userLength = getPathLength(user);
            double refLength = getPathLength(ref);

            List<Double> userStart = user.get(0);
            List<Double> userEnd = user.get(user.size() - 1);
            List<Double> refStart = ref.get(0);
            List<Double> refEnd = ref.get(ref.size() - 1);

            // global direction
            double userAngle = Math.atan2(userEnd.get(1) - userStart.get(1), userEnd.get(0) - userStart.get(0));
            double refAngle = Math.atan2(refEnd.get(1) - refStart.get(1), refEnd.get(0) - refStart.get(0));

            double diff = normalizeAngle(userAngle - refAngle);

            if (Math.abs(diff) > Math.PI / 2.0) {
                return 0.0;
            }

            // --- Anchor filter ---
            double startErr = distance(userStart, refStart) / STROKE_TOLERANCE_POSITION;
            double endErr = distance(userEnd, refEnd) / STROKE_TOLERANCE_POSITION;

            if (startErr > MAX_ALLOWED_ANCHOR_ERROR) return 0.0;
            if (endErr > MAX_ALLOWED_ANCHOR_ERROR) return 0.0;

            // preprocess
            List<List<Double>> u = preprocess(user);
            List<List<Double>> r = preprocess(ref);

            List<Double> turnU = calculateTurningAngles(u);
            List<Double> turnR = calculateTurningAngles(r);

            List<Double> absU = calculateAbsoluteAngles(u);
            List<Double> absR = calculateAbsoluteAngles(r);

            // DTW
            double dtw = dtwDistance(u, turnU, absU, r, turnR, absR);
            double dtwScore = Math.max(0.0, 1.0 - (dtw / RESAMPLE_POINTS_COUNT));

            // length score
            double lengthScore = 1.0;
            if (refLength > 0.001) {
                double ratioError = abs(userLength - refLength) / refLength;
                double penalty = Math.max(0.0, Math.min(1.0, ratioError - LENGTH_ERROR_TOLERANCE));
                lengthScore = 1.0 - penalty;
            } else if (userLength > 5.0) {
                lengthScore = 0.0;
            }

            return WEIGHT_HYBRID_DTW_SHAPE * dtwScore +
                    WEIGHT_HYBRID_LENGTH * lengthScore;
        }

        private static double distance(List<Double> p1, List<Double> p2) {
            double dx = p1.get(0) - p2.get(0);
            double dy = p1.get(1) - p2.get(1);
            return Math.sqrt(dx * dx + dy * dy);
        }

        private static double normalizeAngle(double a) {
            while (a <= -Math.PI) a += 2 * Math.PI;
            while (a > Math.PI) a -= 2 * Math.PI;
            return a;
        }

        private static double getPathLength(List<List<Double>> stroke) {
            double len = 0.0;
            for (int i = 1; i < stroke.size(); i++) {
                len += distance(stroke.get(i - 1), stroke.get(i));
            }
            return len;
        }

        private static List<List<Double>> preprocess(List<List<Double>> s) {
            return resample(s, RESAMPLE_POINTS_COUNT);
        }

        private static List<Double> calculateTurningAngles(List<List<Double>> s) {
            int n = s.size();
            List<Double> out = new ArrayList<>();

            if (n < 3) {
                for (int i = 0; i < n; i++) out.add(0.0);
                return out;
            }

            out.add(0.0);
            for (int i = 1; i < n - 1; i++) {
                List<Double> p0 = s.get(i - 1);
                List<Double> p1 = s.get(i);
                List<Double> p2 = s.get(i + 1);

                double a1 = Math.atan2(p1.get(1) - p0.get(1), p1.get(0) - p0.get(0));
                double a2 = Math.atan2(p2.get(1) - p1.get(1), p2.get(0) - p1.get(0));

                out.add(normalizeAngle(a2 - a1));
            }
            out.add(0.0);
            return out;
        }

        private static List<Double> calculateAbsoluteAngles(List<List<Double>> s) {
            int n = s.size();
            List<Double> out = new ArrayList<>();

            if (n < 2) {
                for (int i = 0; i < n; i++) out.add(0.0);
                return out;
            }

            for (int i = 1; i < n; i++) {
                List<Double> p0 = s.get(i - 1);
                List<Double> p1 = s.get(i);
                out.add(Math.atan2(p1.get(1) - p0.get(1), p1.get(0) - p0.get(0)));
            }
            out.add(out.get(out.size() - 1));

            return out;
        }

        private static List<List<Double>> resample(List<List<Double>> stroke, int n) {
            double total = getPathLength(stroke);

            if (total <= 0.0) {
                List<List<Double>> res = new ArrayList<>();
                if (!stroke.isEmpty()) {
                    List<Double> p = stroke.get(0);
                    for (int i = 0; i < n; i++) {
                        res.add(List.of(p.get(0), p.get(1)));
                    }
                }
                return res;
            }

            double interval = total / (n - 1);

            List<List<Double>> out = new ArrayList<>();
            out.add(stroke.get(0));

            double D = 0.0;

            for (int i = 1; i < stroke.size(); i++) {
                List<Double> p1 = stroke.get(i - 1);
                List<Double> p2 = stroke.get(i);

                double d = distance(p1, p2);

                if (d > 0) {
                    while ((D + d) >= interval) {
                        double t = (interval - D) / d;

                        double qx = p1.get(0) + t * (p2.get(0) - p1.get(0));
                        double qy = p1.get(1) + t * (p2.get(1) - p1.get(1));

                        out.add(List.of(qx, qy));

                        p1 = List.of(qx, qy);
                        d = distance(p1, p2);
                        D = 0.0;

                        if (out.size() == n) return out;
                    }
                }
                D += d;
            }

            while (out.size() < n) {
                List<Double> last = stroke.get(stroke.size() - 1);
                out.add(List.of(last.get(0), last.get(1)));
            }

            return out;
        }

        private static double dtwDistance(List<List<Double>> s1, List<Double> t1, List<Double> a1,
                                          List<List<Double>> s2, List<Double> t2, List<Double> a2) {

            int n = s1.size(), m = s2.size();
            double[][] dtw = new double[n + 1][m + 1];

            for (int i = 0; i <= n; i++)
                for (int j = 0; j <= m; j++)
                    dtw[i][j] = Double.POSITIVE_INFINITY;

            dtw[0][0] = 0.0;

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {

                    double pos = Math.min(1.0, distance(s1.get(i - 1), s2.get(j - 1)) / STROKE_TOLERANCE_POSITION);

                    double da = normalizeAngle(a1.get(i - 1) - a2.get(j - 1));
                    double dir = Math.min(1.0, Math.abs(da) / DIRECTION_TOLERANCE_RADIANS);

                    double dt = normalizeAngle(t1.get(i - 1) - t2.get(j - 1));
                    double turn = Math.min(1.0, Math.abs(dt) / TURNING_TOLERANCE_RADIANS);

                    double wPos, wDir, wTurn;

                    if (Math.abs(t2.get(j - 1)) < STRAIGHTNESS_THRESHOLD_RADIANS) {
                        wPos = WEIGHT_POS_STRAIGHT;
                        wDir = WEIGHT_DIR_STRAIGHT;
                        wTurn = WEIGHT_TURN_STRAIGHT;
                    } else {
                        wPos = WEIGHT_POS_CURVED;
                        wDir = WEIGHT_DIR_CURVED;
                        wTurn = WEIGHT_TURN_CURVED;
                    }

                    double cost = wPos * pos + wDir * dir + wTurn * turn;
                    cost = Math.min(1.0, cost);

                    double prev = Math.min(dtw[i - 1][j],
                            Math.min(dtw[i][j - 1], dtw[i - 1][j - 1]));

                    dtw[i][j] = cost + prev;
                }
            }

            return dtw[n][m];
        }
    }
}
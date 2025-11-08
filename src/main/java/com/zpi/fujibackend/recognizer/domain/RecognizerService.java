package com.zpi.fujibackend.recognizer.domain;

import com.zpi.fujibackend.algorithm.KanjiNormalizer;
import com.zpi.fujibackend.algorithm.PatternFeatureExtractor;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.ReferenceKanjiDto;
import com.zpi.fujibackend.recognizer.RecognizerFacade;
import com.zpi.fujibackend.recognizer.dto.RecognizeForm;
import com.zpi.fujibackend.recognizer.dto.RecognizedKanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class RecognizerService implements RecognizerFacade {

    private final KanjiFacade kanjiFacade;

    @Override
    public List<RecognizedKanjiDto> recognize(RecognizeForm form) {
        final List<List<List<Double>>> userStrokes = form.userStrokes();
        if (userStrokes == null || userStrokes.isEmpty()) {
            return List.of();
        }

        final int strokeCount = userStrokes.size();
        final List<ReferenceKanjiDto> candidateKanjis = kanjiFacade.getKanjiByStrokeNumber(strokeCount);
        if (candidateKanjis.isEmpty()) {
            return List.of();
        }

        final List<List<List<Double>>> userFeatures = PatternFeatureExtractor.extractFeatures(
                KanjiNormalizer.momentNormalize(userStrokes), 20.
        );

        List<ReferenceKanjiDto> normalizedCandidateFeatures = candidateKanjis.stream()
                .map(kanji -> {
                    List<List<List<Double>>> normalized = KanjiNormalizer.momentNormalize(kanji.drawingData());
                    List<List<List<Double>>> features = PatternFeatureExtractor.extractFeatures(normalized, 20.);
                    return new ReferenceKanjiDto(kanji.uuid(), kanji.character(), features);
                })
                .collect(Collectors.toList());

        List<RecognitionCandidate> coarseCandidates = runCoarseClassification(userFeatures, normalizedCandidateFeatures);
        List<RecognitionCandidate> fineCandidates = runFineClassification(userFeatures, coarseCandidates, normalizedCandidateFeatures);

        return fineCandidates.stream()
                .limit(10)
                .map(kanji -> new RecognizedKanjiDto(kanji.uuid(), kanji.kanjiChar()))
                .toList();
    }

    private List<RecognitionCandidate> runCoarseClassification(List<List<List<Double>>> normalizedUserStrokes, List<ReferenceKanjiDto> candidateKanjis) {
        final int strokeCount = normalizedUserStrokes.size();
        List<RecognitionCandidate> candidates = new ArrayList<>();

        for (int i = 0; i < candidateKanjis.size(); i++) {
            ReferenceKanjiDto refKanji = candidateKanjis.get(i);
            List<List<List<Double>>> refFeatures = refKanji.drawingData();

            int[] optimalStrokeMap = findOptimalStrokeMap(
                    refFeatures,
                    normalizedUserStrokes,
                    this::calculateEndpointDistance
            );

            double totalDistance = calculateTotalDistance(
                    refFeatures,
                    normalizedUserStrokes,
                    this::calculateEndpointDistance,
                    optimalStrokeMap
            );

            double normalizedDist = (strokeCount > 0) ? totalDistance / strokeCount : totalDistance;

            candidates.add(new RecognitionCandidate(i, refKanji.uuid(), refKanji.character(), normalizedDist));
        }

        Collections.sort(candidates);
        return candidates;
    }

    private List<RecognitionCandidate> runFineClassification(List<List<List<Double>>> normalizedUserStrokes,
                                                             List<RecognitionCandidate> coarseCandidates,
                                                             List<ReferenceKanjiDto> allCandidateKanjis) {

        List<RecognitionCandidate> fineCandidates = new ArrayList<>();
        int count = Math.min(coarseCandidates.size(), 100); // Tylko Top 100

        for (int i = 0; i < count; i++) {
            // Pobierz indeks kandydata z listy 'allCandidateKanjis'
            int refIndex = coarseCandidates.get(i).referenceIndex();
            ReferenceKanjiDto refKanji = allCandidateKanjis.get(refIndex);
            List<List<List<Double>>> refFeatures = refKanji.drawingData();
            ;

            // Krok 3 (ponownie): Znajdź optymalne dopasowanie,
            // tym razem używając lepszej metryki 'InitialDistance'
            int[] optimalStrokeMap = findOptimalStrokeMap(
                    refFeatures,
                    normalizedUserStrokes,
                    this::calculateInitialStrokeDistance // Lepsza metryka do mapowania
            );

            // Oblicz końcowy, precyzyjny dystans
            double finalDistance = calculateDetailedTotalDistance(
                    refFeatures,
                    normalizedUserStrokes,
                    optimalStrokeMap
            );

            // 'calculateDetailedTotalDistance' już zawiera normalizację przez liczbę kresek

            fineCandidates.add(new RecognitionCandidate(refIndex, refKanji.uuid(), refKanji.character(), finalDistance));
        }

        Collections.sort(fineCandidates);
        return fineCandidates;
    }


    private int[] findOptimalStrokeMap(List<List<List<Double>>> pattern1, List<List<List<Double>>> pattern2,
                                       StrokeDistanceMetric strokeDistanceMetric) {

        SortedPatternPair patterns = sortPatternsByStrokeCount(pattern1, pattern2);
        final int L = 3;

        int[] strokeMap = createInitialStrokeMap(patterns, strokeDistanceMetric);

        for (int l = 0; l < L; l++) {
            for (int i = 0; i < strokeMap.length; i++) {
                if (strokeMap[i] != -1) {
                    double dii = strokeDistanceMetric.apply(
                            patterns.pattern1().get(i),
                            patterns.pattern2().get(strokeMap[i])
                    );

                    for (int j = 0; j < strokeMap.length; j++) {
                        if (i != j && strokeMap[j] != -1) {

                            double djj = strokeDistanceMetric.apply(patterns.pattern1().get(j), patterns.pattern2().get(strokeMap[j]));
                            double dij = strokeDistanceMetric.apply(patterns.pattern1().get(j), patterns.pattern2().get(strokeMap[i]));
                            double dji = strokeDistanceMetric.apply(patterns.pattern1().get(i), patterns.pattern2().get(strokeMap[j]));

                            if (dji + dij < dii + djj) {
                                // Tak, zamiana jest lepsza!
                                int tempMapJ = strokeMap[j];
                                strokeMap[j] = strokeMap[i];
                                strokeMap[i] = tempMapJ;
                                dii = dij;
                            }
                        }
                    }
                }
            }
        }
        return strokeMap;
    }

    private int[] createInitialStrokeMap(SortedPatternPair patterns, StrokeDistanceMetric strokeDistanceMetric) {
        int[] strokeMap = new int[patterns.pattern1StrokeCount()];
        Arrays.fill(strokeMap, -1);

        boolean[] isPattern1StrokeTaken = new boolean[patterns.pattern1StrokeCount()];
        Arrays.fill(isPattern1StrokeTaken, false);

        for (int i = 0; i < patterns.pattern2StrokeCount(); i++) {
            double minDistance = Double.POSITIVE_INFINITY;
            int bestMatchIndex = -1;

            for (int j = 0; j < patterns.pattern1StrokeCount(); j++) {
                if (!isPattern1StrokeTaken[j]) {
                    double d = strokeDistanceMetric.apply(
                            patterns.pattern1().get(j),
                            patterns.pattern2().get(i)
                    );
                    if (d < minDistance) {
                        minDistance = d;
                        bestMatchIndex = j;
                    }
                }
            }
            if (bestMatchIndex != -1) {
                isPattern1StrokeTaken[bestMatchIndex] = true;
                strokeMap[bestMatchIndex] = i;
            }
        }
        return strokeMap;
    }


    private double calculateTotalDistance(List<List<List<Double>>> pattern1, List<List<List<Double>>> pattern2, StrokeDistanceMetric metric, int[] strokeMap) {

        SortedPatternPair patterns = sortPatternsByStrokeCount(pattern1, pattern2);
        double totalDistance = 0;

        // Przechodzimy przez mapę. Długość mapy = pattern1StrokeCount
        for (int i = 0; i < patterns.pattern1StrokeCount(); i++) {
            int j = strokeMap[i]; // j to pasujący indeks z pattern2
            if (j != -1) { // -1 oznacza brak pary (co się nie zdarzy przy n==m)
                totalDistance += metric.apply(
                        patterns.pattern1().get(i),
                        patterns.pattern2().get(j)
                );
            }
        }
        return totalDistance;
    }

    private double calculateDetailedTotalDistance(List<List<List<Double>>> pattern1, List<List<List<Double>>> pattern2, int[] strokeMap) {

        SortedPatternPair patterns = sortPatternsByStrokeCount(pattern1, pattern2);
        double totalDistance = 0;

        for (int i = 0; i < patterns.pattern1StrokeCount(); i++) {
            int j = strokeMap[i];
            if (j != -1) {
                totalDistance += calculateWholeStrokeDistance(
                        patterns.pattern1().get(i),
                        patterns.pattern2().get(j)
                );
            }
        }

        return (patterns.pattern2StrokeCount() > 0)
                ? totalDistance / patterns.pattern2StrokeCount()
                : totalDistance;
    }

    private double calculateEndpointDistance(List<List<Double>> stroke1, List<List<Double>> stroke2) {
        int l1 = (stroke1 == null) ? 0 : stroke1.size();
        int l2 = (stroke2 == null) ? 0 : stroke2.size();
        if (l1 == 0 || l2 == 0) return 0;

        double dist = 0;
        List<Double> p1Start = stroke1.get(0);
        List<Double> p2Start = stroke2.get(0);
        dist += (Math.abs(p1Start.get(0) - p2Start.get(0)) + Math.abs(p1Start.get(1) - p2Start.get(1)));

        List<Double> p1End = stroke1.get(l1 - 1);
        List<Double> p2End = stroke2.get(l2 - 1);
        dist += (Math.abs(p1End.get(0) - p2End.get(0)) + Math.abs(p1End.get(1) - p2End.get(1)));

        return dist;
    }


    private double calculateInitialStrokeDistance(List<List<Double>> stroke1, List<List<Double>> stroke2) {
        int l1 = (stroke1 == null) ? 0 : stroke1.size();
        int l2 = (stroke2 == null) ? 0 : stroke2.size();
        if (l1 == 0 || l2 == 0) return 0;

        int lmin = Math.min(l1, l2);
        int lmax = Math.max(l1, l2);
        double dist = 0;

        for (int i = 0; i < lmin; i++) {
            List<Double> p1 = stroke1.get(i);
            List<Double> p2 = stroke2.get(i);
            dist += (Math.abs(p1.get(0) - p2.get(0)) + Math.abs(p1.get(1) - p2.get(1)));
        }
        return dist * ((double) lmax / lmin);
    }

    private double calculateWholeStrokeDistance(List<List<Double>> stroke1, List<List<Double>> stroke2) {
        SortedStrokePair strokes = sortStrokesByPointCount(stroke1, stroke2);
        if (strokes.shorterStrokePointCount() == 0) {
         return 0;
        }

        double dist = 0;
        for (int i = 0; i < strokes.shorterStrokePointCount(); i++) {
            int j = (int) (((double) strokes.longerStrokePointCount() / strokes.shorterStrokePointCount()) * i);

            List<Double> p1 = strokes.longerStroke().get(j);
            List<Double> p2 = strokes.shorterStroke().get(i);
            dist += (Math.abs(p1.get(0) - p2.get(0)) + Math.abs(p1.get(1) - p2.get(1)));
        }
        return (dist / strokes.shorterStrokePointCount());
    }


    private SortedPatternPair sortPatternsByStrokeCount(List<List<List<Double>>> pattern1, List<List<List<Double>>> pattern2) {
        int l1 = (pattern1 == null) ? 0 : pattern1.size();
        int l2 = (pattern2 == null) ? 0 : pattern2.size();
        if (l1 < l2) {
            return new SortedPatternPair(pattern2, pattern1, l2, l1);
        } else {
            return new SortedPatternPair(pattern1, pattern2, l1, l2);
        }
    }

    private SortedStrokePair sortStrokesByPointCount(List<List<Double>> stroke1, List<List<Double>> stroke2) {
        int l1 = (stroke1 == null) ? 0 : stroke1.size();
        int l2 = (stroke2 == null) ? 0 : stroke2.size();
        if (l1 < l2) {
            return new SortedStrokePair(stroke2, stroke1, l2, l1);
        } else {
            return new SortedStrokePair(stroke1, stroke2, l1, l2);
        }
    }
}

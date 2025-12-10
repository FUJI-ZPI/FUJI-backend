package com.zpi.fujibackend.recognizer.domain;

import com.zpi.fujibackend.algorithm.KanjiNormalizer;
import com.zpi.fujibackend.algorithm.PatternFeatureExtractor;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.ReferenceKanjiDto;
import com.zpi.fujibackend.recognizer.dto.RecognizeForm;
import com.zpi.fujibackend.recognizer.dto.RecognizedKanjiDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class RecognizerServiceTest {

    @Mock
    private KanjiFacade kanjiFacade;

    @InjectMocks
    private RecognizerService recognizerService;

    @Test
    void recognize_ShouldReturnEmptyList_WhenUserStrokesAreNull() {
        RecognizeForm form = new RecognizeForm(null);

        List<RecognizedKanjiDto> result = recognizerService.recognize(form);

        assertThat(result).isEmpty();
    }

    @Test
    void recognize_ShouldReturnEmptyList_WhenUserStrokesAreEmpty() {
        RecognizeForm form = new RecognizeForm(Collections.emptyList());

        List<RecognizedKanjiDto> result = recognizerService.recognize(form);

        assertThat(result).isEmpty();
    }

    @Test
    void recognize_ShouldReturnEmptyList_WhenNoCandidateKanjisFound() {
        List<List<List<Double>>> userStrokes = List.of(List.of(List.of(0.0, 0.0)));
        RecognizeForm form = new RecognizeForm(userStrokes);

        given(kanjiFacade.getKanjiByStrokeNumber(1)).willReturn(Collections.emptyList());

        List<RecognizedKanjiDto> result = recognizerService.recognize(form);

        assertThat(result).isEmpty();
    }

    @Test
    void recognize_ShouldReturnSortedCandidates_WhenClassificationIsSuccessful() {
        List<List<List<Double>>> userStrokes = createStrokes(0.0, 0.0);
        RecognizeForm form = new RecognizeForm(userStrokes);

        UUID uuidBest = UUID.randomUUID();
        UUID uuidWorst = UUID.randomUUID();

        List<List<List<Double>>> bestMatchData = createStrokes(0.1, 0.1);
        List<List<List<Double>>> worstMatchData = createStrokes(100.0, 100.0);

        ReferenceKanjiDto bestCandidate = new ReferenceKanjiDto(uuidBest, "A", bestMatchData);
        ReferenceKanjiDto worstCandidate = new ReferenceKanjiDto(uuidWorst, "B", worstMatchData);

        given(kanjiFacade.getKanjiByStrokeNumber(1)).willReturn(List.of(worstCandidate, bestCandidate));

        try (MockedStatic<KanjiNormalizer> normalizer = mockStatic(KanjiNormalizer.class);
             MockedStatic<PatternFeatureExtractor> extractor = mockStatic(PatternFeatureExtractor.class)) {

            normalizer.when(() -> KanjiNormalizer.momentNormalize(anyList()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            extractor.when(() -> PatternFeatureExtractor.extractFeatures(anyList(), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            List<RecognizedKanjiDto> result = recognizerService.recognize(form);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).uuid()).isEqualTo(uuidBest);
            assertThat(result.get(1).uuid()).isEqualTo(uuidWorst);
        }
    }

    @Test
    void recognize_ShouldLimitResultsToTen() {
        List<List<List<Double>>> userStrokes = createStrokes(0.0, 0.0);
        RecognizeForm form = new RecognizeForm(userStrokes);

        List<ReferenceKanjiDto> candidates = IntStream.range(0, 15)
                .mapToObj(i -> new ReferenceKanjiDto(
                        UUID.randomUUID(),
                        String.valueOf(i),
                        createStrokes(i * 10.0, i * 10.0)
                ))
                .toList();

        given(kanjiFacade.getKanjiByStrokeNumber(1)).willReturn(candidates);

        try (MockedStatic<KanjiNormalizer> normalizer = mockStatic(KanjiNormalizer.class);
             MockedStatic<PatternFeatureExtractor> extractor = mockStatic(PatternFeatureExtractor.class)) {

            normalizer.when(() -> KanjiNormalizer.momentNormalize(anyList()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            extractor.when(() -> PatternFeatureExtractor.extractFeatures(anyList(), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            List<RecognizedKanjiDto> result = recognizerService.recognize(form);

            assertThat(result).hasSize(10);
            assertThat(result.get(0).character()).isEqualTo("0");
        }
    }

    @Test
    void recognize_ShouldHandleDifferentStrokeCountsAndOptimization() {
        List<List<List<Double>>> userStrokes = createMultiStrokes(
                new double[]{0.0, 0.0},
                new double[]{10.0, 10.0}
        );
        RecognizeForm form = new RecognizeForm(userStrokes);

        List<List<List<Double>>> candidateData = createMultiStrokes(
                new double[]{10.0, 10.0},
                new double[]{0.0, 0.0}
        );

        ReferenceKanjiDto candidate = new ReferenceKanjiDto(UUID.randomUUID(), "Swap", candidateData);

        given(kanjiFacade.getKanjiByStrokeNumber(2)).willReturn(List.of(candidate));

        try (MockedStatic<KanjiNormalizer> normalizer = mockStatic(KanjiNormalizer.class);
             MockedStatic<PatternFeatureExtractor> extractor = mockStatic(PatternFeatureExtractor.class)) {

            normalizer.when(() -> KanjiNormalizer.momentNormalize(anyList()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            extractor.when(() -> PatternFeatureExtractor.extractFeatures(anyList(), anyDouble()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            List<RecognizedKanjiDto> result = recognizerService.recognize(form);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).character()).isEqualTo("Swap");
        }
    }

    private List<List<List<Double>>> createStrokes(double x, double y) {
        return List.of(List.of(List.of(x, y)));
    }

    private List<List<List<Double>>> createMultiStrokes(double[] p1, double[] p2) {
        return List.of(
                List.of(List.of(p1[0], p1[1])),
                List.of(List.of(p2[0], p2[1]))
        );
    }
}
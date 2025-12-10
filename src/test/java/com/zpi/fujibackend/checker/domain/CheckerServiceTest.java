package com.zpi.fujibackend.checker.domain;

import com.zpi.fujibackend.activity.ActivityFacade;
import com.zpi.fujibackend.activity.dto.ActivityForm;
import com.zpi.fujibackend.activity.dto.ActivityType;
import com.zpi.fujibackend.algorithm.KanjiAccuracy;
import com.zpi.fujibackend.checker.dto.CheckKanjiForm;
import com.zpi.fujibackend.checker.dto.CheckStrokeForm;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.srs.domain.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckerServiceTest {

    @Mock
    private SrsFacade srsFacade;

    @Mock
    private KanjiFacade kanjiFacade;

    @Mock
    private ActivityFacade activityFacade;

    @InjectMocks
    private CheckerService checkerService;

    @Captor
    private ArgumentCaptor<ActivityForm> activityFormCaptor;

    private final UUID kanjiUuid = UUID.randomUUID();
    private final List<List<List<Double>>> userStrokes = Collections.emptyList();
    private final List<List<List<Double>>> referenceStrokes = Collections.emptyList();


    @Test
    void shouldReturnResultAndSkipSrsUpdate_WhenLearningSessionAndAccuracyBelowThreshold() {
        CheckKanjiForm form = new CheckKanjiForm(kanjiUuid, userStrokes, referenceStrokes, true);
        Kanji kanji = new Kanji();
        kanji.setUuid(kanjiUuid);

        given(kanjiFacade.getKanjiByUuid(kanjiUuid)).willReturn(kanji);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult mockResult = new KanjiAccuracy.KanjiAccuracyResult(0.699, List.of(0.699));
            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokes, referenceStrokes))
                    .thenReturn(mockResult);

            KanjiAccuracy.KanjiAccuracyResult result = checkerService.checkKanji(form);

            assertThat(result.overallAccuracy()).isEqualTo(0.699);
            verify(srsFacade, never()).addCard(any());
            verify(srsFacade, never()).increaseFamiliarity(any());
            verify(srsFacade, never()).decreaseFamiliarity(any());
            verify(activityFacade, never()).addActivity(any());
        }
    }

    @Test
    void shouldAddCardAndSaveLessonActivity_WhenLearningSessionAndAccuracyAboveThreshold() {
        CheckKanjiForm form = new CheckKanjiForm(kanjiUuid, userStrokes, referenceStrokes, true);
        Kanji kanji = new Kanji();
        kanji.setUuid(kanjiUuid);
        Card createdCard = new Card();

        given(kanjiFacade.getKanjiByUuid(kanjiUuid)).willReturn(kanji);
        given(srsFacade.addCard(kanjiUuid)).willReturn(createdCard);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult mockResult = new KanjiAccuracy.KanjiAccuracyResult(0.701, List.of(0.701));
            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokes, referenceStrokes))
                    .thenReturn(mockResult);

            checkerService.checkKanji(form);

            verify(srsFacade).addCard(kanjiUuid);
            verify(activityFacade).addActivity(activityFormCaptor.capture());

            ActivityForm capturedActivity = activityFormCaptor.getValue();
            assertThat(capturedActivity.activityType()).isEqualTo(ActivityType.LESSON);
            assertThat(capturedActivity.isSuccess()).isTrue();
            assertThat(capturedActivity.card()).isEqualTo(createdCard);
        }
    }

    @Test
    void shouldIncreaseFamiliarityAndSaveReviewActivity_WhenReviewSessionAndSuccess() {
        CheckKanjiForm form = new CheckKanjiForm(kanjiUuid, userStrokes, referenceStrokes, false);
        Kanji kanji = new Kanji();
        kanji.setUuid(kanjiUuid);
        Card updatedCard = new Card();

        given(kanjiFacade.getKanjiByUuid(kanjiUuid)).willReturn(kanji);
        given(srsFacade.increaseFamiliarity(kanjiUuid)).willReturn(updatedCard);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult mockResult = new KanjiAccuracy.KanjiAccuracyResult(0.85, List.of(0.85));
            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokes, referenceStrokes))
                    .thenReturn(mockResult);

            checkerService.checkKanji(form);

            verify(srsFacade).increaseFamiliarity(kanjiUuid);
            verify(srsFacade, never()).decreaseFamiliarity(any());

            verify(activityFacade).addActivity(activityFormCaptor.capture());
            ActivityForm capturedActivity = activityFormCaptor.getValue();
            assertThat(capturedActivity.activityType()).isEqualTo(ActivityType.REVIEW);
            assertThat(capturedActivity.isSuccess()).isTrue();
        }
    }

    @Test
    void shouldDecreaseFamiliarityAndSaveReviewActivity_WhenReviewSessionAndFailure() {
        CheckKanjiForm form = new CheckKanjiForm(kanjiUuid, userStrokes, referenceStrokes, false);
        Kanji kanji = new Kanji();
        kanji.setUuid(kanjiUuid);
        Card updatedCard = new Card();

        given(kanjiFacade.getKanjiByUuid(kanjiUuid)).willReturn(kanji);
        given(srsFacade.decreaseFamiliarity(kanjiUuid)).willReturn(updatedCard);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult mockResult = new KanjiAccuracy.KanjiAccuracyResult(0.50, List.of(0.50));
            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokes, referenceStrokes))
                    .thenReturn(mockResult);

            checkerService.checkKanji(form);

            verify(srsFacade).decreaseFamiliarity(kanjiUuid);
            verify(srsFacade, never()).increaseFamiliarity(any());

            verify(activityFacade).addActivity(activityFormCaptor.capture());
            ActivityForm capturedActivity = activityFormCaptor.getValue();
            assertThat(capturedActivity.activityType()).isEqualTo(ActivityType.REVIEW);
            assertThat(capturedActivity.isSuccess()).isFalse();
        }
    }

    @Test
    void shouldNotSaveActivity_WhenSrsReturnsNullCard() {
        CheckKanjiForm form = new CheckKanjiForm(kanjiUuid, userStrokes, referenceStrokes, true);
        Kanji kanji = new Kanji();
        kanji.setUuid(kanjiUuid);

        given(kanjiFacade.getKanjiByUuid(kanjiUuid)).willReturn(kanji);
        given(srsFacade.addCard(kanjiUuid)).willReturn(null);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult mockResult = new KanjiAccuracy.KanjiAccuracyResult(0.99, List.of(0.99));
            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokes, referenceStrokes))
                    .thenReturn(mockResult);

            checkerService.checkKanji(form);

            verify(srsFacade).addCard(kanjiUuid);
            verify(activityFacade, never()).addActivity(any());
        }
    }


    @Test
    void checkStroke_ShouldWrapStrokesAndCallCalculator() {
        List<List<Double>> singleUserStroke = Collections.emptyList();
        List<List<Double>> singleRefStroke = Collections.emptyList();
        CheckStrokeForm form = new CheckStrokeForm(singleUserStroke, singleRefStroke);

        try (MockedStatic<KanjiAccuracy.KanjiComparator> mockedComparator = Mockito.mockStatic(KanjiAccuracy.KanjiComparator.class)) {
            KanjiAccuracy.KanjiAccuracyResult expectedResult = new KanjiAccuracy.KanjiAccuracyResult(1.0, List.of(1.0));

            mockedComparator.when(() -> KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(
                    eq(List.of(singleUserStroke)),
                    eq(List.of(singleRefStroke)))
            ).thenReturn(expectedResult);

            KanjiAccuracy.KanjiAccuracyResult result = checkerService.checkStroke(form);

            assertThat(result).isEqualTo(expectedResult);
        }
    }
}
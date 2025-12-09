package com.zpi.fujibackend.activity.domain;

import com.zpi.fujibackend.activity.dto.ActivityForm;
import com.zpi.fujibackend.activity.dto.ActivityPlaybackDetails;
import com.zpi.fujibackend.activity.dto.ActivityType;
import com.zpi.fujibackend.activity.dto.DailyActivityDetail;
import com.zpi.fujibackend.activity.dto.DailyActivityStat;
import com.zpi.fujibackend.algorithm.KanjiNormalizer;
import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.srs.domain.Card;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserFacade userFacade;

    @Mock
    private ProgressFacade progressFacade;

    @InjectMocks
    private ActivityService activityService;


    @Test
    void addActivity_ShouldSaveActivityAndUpdateStreak_WhenSuccess() {
        User user = new User();
        user.setUuid(UUID.randomUUID());

        Card card = new Card();
        card.setUser(user);

        ActivityForm form = new ActivityForm(
                card,
                ActivityType.LESSON,
                Collections.emptyList(),
                Collections.emptyList(),
                0.95,
                true
        );

        activityService.addActivity(form);

        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(activityCaptor.capture());

        Activity savedActivity = activityCaptor.getValue();
        assertThat(savedActivity.getCard()).isEqualTo(card);
        assertThat(savedActivity.getActivityType()).isEqualTo(ActivityType.LESSON);
        assertThat(savedActivity.getOverallAccuracy()).isEqualTo(0.95);

        verify(progressFacade).updateDailyStreak(eq(user), any(Instant.class));
    }

    @Test
    void addActivity_ShouldSaveActivityAndSkipStreakUpdate_WhenFailure() {
        User user = new User();
        Card card = new Card();
        card.setUser(user);

        ActivityForm form = new ActivityForm(
                card,
                ActivityType.REVIEW,
                Collections.emptyList(),
                Collections.emptyList(),
                0.40,
                false
        );

        activityService.addActivity(form);

        verify(activityRepository).save(any(Activity.class));
        verify(progressFacade, never()).updateDailyStreak(any(), any());
    }


    @Test
    void getLast110DaysStats_ShouldFillMissingDaysWithZero() {
        User user = new User();
        given(userFacade.getCurrentUser()).willReturn(user);

        LocalDate today = LocalDate.of(2023, 10, 20);
        LocalDate activeDate = today.minusDays(5);

        DailyActivityStat dbStat = new DailyActivityStat(activeDate, 10L);
        given(activityRepository.findStatsByUser(eq(user), any(Instant.class)))
                .willReturn(List.of(dbStat));

        List<DailyActivityStat> result = activityService.getLast110DaysStats(today);

        assertThat(result).hasSize(111);

        assertThat(result.getFirst().count()).isEqualTo(0);
        assertThat(result.getFirst().date()).isEqualTo(today.minusDays(110));

        DailyActivityStat activeDayStat = result.stream()
                .filter(s -> s.date().equals(activeDate))
                .findFirst()
                .orElseThrow();
        assertThat(activeDayStat.count()).isEqualTo(10L);

        assertThat(result.getLast().date()).isEqualTo(today);
    }


    @Test
    void getActivityDetailsForDay_ShouldQueryCorrectTimeRange() {
        User user = new User();
        given(userFacade.getCurrentUser()).willReturn(user);

        LocalDate day = LocalDate.of(2023, 10, 20);
        DailyActivityDetail detail = mock(DailyActivityDetail.class);

        given(activityRepository.findDetailsForDay(eq(user), any(Instant.class), any(Instant.class)))
                .willReturn(List.of(detail));

        List<DailyActivityDetail> result = activityService.getActivityDetailsForDay(day);

        assertThat(result).hasSize(1);

        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(activityRepository).findDetailsForDay(eq(user), instantCaptor.capture(), instantCaptor.capture());

        List<Instant> capturedInstants = instantCaptor.getAllValues();
        Instant start = capturedInstants.get(0);
        Instant end = capturedInstants.get(1);

        assertThat(start).isEqualTo(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertThat(end).isEqualTo(day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    @Test
    void getActivityDetails_ShouldReturnNormalizedDetails() {
        UUID activityUuid = UUID.randomUUID();
        User user = new User();
        given(userFacade.getCurrentUser()).willReturn(user);

        List<List<List<Double>>> rawStrokes = List.of(List.of(List.of(1.0, 1.0)));
        List<List<List<Double>>> normalizedStrokes = List.of(List.of(List.of(0.5, 0.5)));

        ActivityPlaybackDetails rawDetails = mock(ActivityPlaybackDetails.class);
        given(rawDetails.referenceStrokes()).willReturn(rawStrokes);

        ActivityPlaybackDetails finalDetails = mock(ActivityPlaybackDetails.class);
        given(rawDetails.withNormalizedReference(normalizedStrokes)).willReturn(finalDetails);

        given(activityRepository.findPlaybackDetails(activityUuid, user))
                .willReturn(Optional.of(rawDetails));

        try (MockedStatic<KanjiNormalizer> mockedNormalizer = mockStatic(KanjiNormalizer.class)) {
            mockedNormalizer.when(() -> KanjiNormalizer.momentNormalize(rawStrokes))
                    .thenReturn(normalizedStrokes);

            ActivityPlaybackDetails result = activityService.getActivityDetails(activityUuid);

            assertThat(result).isEqualTo(finalDetails);
            verify(activityRepository).findPlaybackDetails(activityUuid, user);
        }
    }

    @Test
    void getActivityDetails_ShouldThrowException_WhenNotFound() {
        UUID activityUuid = UUID.randomUUID();
        User user = new User();
        given(userFacade.getCurrentUser()).willReturn(user);

        given(activityRepository.findPlaybackDetails(activityUuid, user))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.getActivityDetails(activityUuid))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(activityUuid.toString());
    }
}
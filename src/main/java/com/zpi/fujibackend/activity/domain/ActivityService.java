package com.zpi.fujibackend.activity.domain;

import com.zpi.fujibackend.activity.ActivityFacade;
import com.zpi.fujibackend.activity.dto.*;
import com.zpi.fujibackend.algorithm.KanjiNormalizer;
import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ActivityService implements ActivityFacade {

    private final ActivityRepository activityRepository;
    private final UserFacade userFacade;
    private final ProgressFacade progressFacade;

    @Override
    public void addActivity(ActivityForm form) {
        Instant currentTimestamp =  Instant.now();

        Activity activity = new Activity(
                form.card(),
                form.activityType(),
                currentTimestamp,
                form.drawingData(),
                form.strokesAccuracy(),
                form.overallAccuracy()
        );
        activityRepository.save(activity);

        if (form.isSuccess()) {
            progressFacade.updateDailyStreak(form.card().getUser(), currentTimestamp);
        }
    }

    @Override
    public List<DailyActivityStat> getLast110DaysStats(LocalDate day) {
        User user = userFacade.getCurrentUser();

        LocalDate startDate = day.minusDays(110);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<DailyActivityStat> dbStats = activityRepository.findStatsByUser(user, startInstant);

        Map<LocalDate, Long> statsMap = dbStats.stream()
                .collect(Collectors.toMap(DailyActivityStat::date, DailyActivityStat::count));

        List<DailyActivityStat> fullStats = new ArrayList<>();

        for (int i = 0; i <= 110; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            long count = statsMap.getOrDefault(currentDate, 0L);

            fullStats.add(new DailyActivityStat(currentDate, count));
        }

        return fullStats;
    }

    @Override
    public List<DailyActivityDetail> getActivityDetailsForDay(LocalDate day) {
        final User user = userFacade.getCurrentUser();

        Instant startOfDay = day.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return activityRepository.findDetailsForDay(user, startOfDay, endOfDay);
    }

    @Override
    public ActivityPlaybackDetails getActivityDetails(UUID activityUuid) {
        User user = userFacade.getCurrentUser();

        ActivityPlaybackDetails rawDetails = activityRepository.findPlaybackDetails(activityUuid, user)
                .orElseThrow(() -> new NotFoundException("Activity not found for UUID: " + activityUuid));

        List<List<List<Double>>> normalizedReference =
                KanjiNormalizer.momentNormalize(rawDetails.referenceStrokes());

        return rawDetails.withNormalizedReference(normalizedReference);
    }
}

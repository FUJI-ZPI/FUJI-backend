package com.zpi.fujibackend.activity.controller;

import com.zpi.fujibackend.activity.ActivityFacade;
import com.zpi.fujibackend.activity.dto.ActivityPlaybackDetails;
import com.zpi.fujibackend.activity.dto.DailyActivityDetail;
import com.zpi.fujibackend.activity.dto.DailyActivityStat;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
class ActivityController {

    private final ActivityFacade activityFacade;

    private static final class Routes {
        static final String STATS = "/stats";
        static final String HISTORY = "/history/{date}";
        static final String DETAILS = "/{uuid}";
    }

    @GetMapping(Routes.STATS)
    List<DailyActivityStat> getLast110DaysStats(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate referenceDate = (date != null) ? date : LocalDate.now();
        return activityFacade.getLast110DaysStats(referenceDate);
    }

    @GetMapping(Routes.HISTORY)
    List<DailyActivityDetail> getActivityDetailsForDay(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return activityFacade.getActivityDetailsForDay(date);
    }

    @GetMapping(Routes.DETAILS)
    ActivityPlaybackDetails getActivityPlaybackDetails(@PathVariable UUID uuid) {
        return activityFacade.getActivityDetails(uuid);
    }
}

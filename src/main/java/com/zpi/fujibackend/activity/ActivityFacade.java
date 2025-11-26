package com.zpi.fujibackend.activity;

import com.zpi.fujibackend.activity.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ActivityFacade {

    void addActivity(ActivityForm form);

    List<DailyActivityStat> getLast110DaysStats(LocalDate day);

    List<DailyActivityDetail> getActivityDetailsForDay(LocalDate day);

    ActivityPlaybackDetails getActivityDetails(UUID activityUuid);
}

package com.zpi.fujibackend.activity.dto;

import java.time.Instant;
import java.util.UUID;

public record DailyActivityDetail(UUID activityUuid,
                                  Instant timestamp,
                                  String kanjiCharacter,
                                  ActivityType type,
                                  double accuracy) {

}
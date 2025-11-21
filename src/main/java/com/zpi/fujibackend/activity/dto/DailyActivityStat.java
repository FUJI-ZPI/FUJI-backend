package com.zpi.fujibackend.activity.dto;

import java.time.LocalDate;

public record DailyActivityStat(LocalDate date,
                                Long count) {

}
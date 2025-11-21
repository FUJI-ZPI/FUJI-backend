package com.zpi.fujibackend.checker.dto;

import java.util.List;
import java.util.UUID;

public record CheckKanjiForm(UUID kaniUuid,
                             List<List<List<Double>>> userStrokes,
                             List<List<List<Double>>> referenceStrokes,
                             boolean isLearningSession) {
}

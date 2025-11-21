package com.zpi.fujibackend.checker.dto;

import java.util.List;

public record CheckStrokeForm(List<List<Double>> userStroke,
                              List<List<Double>> referenceStroke) {
}

package com.zpi.fujibackend.recognizer.dto;

import java.util.List;

public record RecognizeForm(List<List<List<Double>>> userStrokes) {
}

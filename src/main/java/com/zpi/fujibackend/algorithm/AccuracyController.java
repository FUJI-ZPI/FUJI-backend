package com.zpi.fujibackend.algorithm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accuracy")
@RequiredArgsConstructor
class AccuracyController {

    @PostMapping("/kanji")
    KanjiAccuracy.KanjiAccuracyResult kanji(@RequestBody AccuracyForm form) {
        return KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(form.userStrokes(), form.referenceStrokes());
    }

    @PostMapping("/stroke")
    KanjiAccuracy.KanjiAccuracyResult stroke(@RequestBody AccuracyStrokeForm form) {
        List<List<List<Double>>> userStrokeList = List.of(form.userStroke());
        List<List<List<Double>>> referenceStrokeList = List.of(form.referenceStroke());

        return KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokeList, referenceStrokeList);
    }
}

package com.zpi.fujibackend.checker.controller;

import com.zpi.fujibackend.algorithm.KanjiAccuracy;
import com.zpi.fujibackend.checker.CheckerFacade;
import com.zpi.fujibackend.checker.dto.CheckKanjiForm;
import com.zpi.fujibackend.checker.dto.CheckStrokeForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checker")
@RequiredArgsConstructor
class CheckerController {

    private final CheckerFacade checkerFacade;

    @PostMapping("/kanji")
    KanjiAccuracy.KanjiAccuracyResult kanji(@RequestBody CheckKanjiForm form) {
        return checkerFacade.checkKanji(form);
    }

    @PostMapping("/stroke")
    KanjiAccuracy.KanjiAccuracyResult stroke(@RequestBody CheckStrokeForm form) {
        return checkerFacade.checkStroke(form);
    }
}

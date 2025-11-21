package com.zpi.fujibackend.srs.controller;

import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.srs.dto.CardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/srs")
@RequiredArgsConstructor
class SrsController {
    private final SrsFacade srsFacade;

    private final static class Routes {
        private static final String REVIEW_BATCH = "/review-batch";
        private static final String LESSON_BATCH = "/lesson-batch";
    }

    @GetMapping(Routes.REVIEW_BATCH)
    List<CardDto> getReviewBatch(@RequestParam(defaultValue = "30") int size) {
        return srsFacade.getReviewBatch(size);
    }

    @GetMapping(Routes.LESSON_BATCH)
    List<KanjiDetailDto> getLessonBatch(@RequestParam(defaultValue = "5") int size) {
        return srsFacade.getLessonBatch(size);
    }
}

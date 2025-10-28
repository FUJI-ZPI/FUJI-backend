package com.zpi.fujibackend.srs.controller;

import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.srs.SrsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/srs")
@RequiredArgsConstructor
class SrsController {
    private final SrsFacade srsFacade;

    private final static class Routes {
        public static final String REVIEW_BATCH = "/review-batch";
        public static final String LESSON_BATCH = "/lesson-batch";
        public static final String INCREASE_FAMILIARITY = "/increase-familiarity";
        public static final String DECREASE_FAMILIARITY = "/decrease-familiarity";
        public static final String ADD_CARD = "/add-card";
    }

    @GetMapping(Routes.REVIEW_BATCH)
    List<KanjiDto> getReviewBatch(@RequestParam(defaultValue = "30") int size) {
        return srsFacade.getReviewBatch(size);
    }

    @GetMapping(Routes.LESSON_BATCH)
    List<KanjiDto> getLessonBatch(@RequestParam(defaultValue = "5") int size) {
        return srsFacade.getLessonBatch(size);
    }

    @PostMapping(Routes.INCREASE_FAMILIARITY)
    void increaseFamiliarity(@RequestParam UUID uuid) {
        srsFacade.increaseFamiliarity(uuid);
    }

    @PostMapping(Routes.DECREASE_FAMILIARITY)
    void decreaseFamiliarity(@RequestParam UUID uuid) {
        srsFacade.decreaseFamiliarity(uuid);
    }

    @PutMapping(Routes.ADD_CARD)
    void addCard(@RequestParam UUID kanjiUuid) {
        srsFacade.addCard(kanjiUuid);
    }
}

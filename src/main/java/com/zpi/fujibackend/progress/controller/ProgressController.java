package com.zpi.fujibackend.progress.controller;

import com.zpi.fujibackend.progress.ProgressFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressFacade progressFacade;

    private static final class Routes {
        static final String STREAK = "/daily-streak";
        static final String KANJI_LEARNED = "/kanji-learned";
    }

    @GetMapping(Routes.STREAK)
    Integer getDailyStreak() {
       return progressFacade.getDailyStreak();
    }

    @GetMapping(Routes.KANJI_LEARNED)
    long getKanjiLearnedAmount() {
        return progressFacade.getKanjiLearnedAmount();
    }
}

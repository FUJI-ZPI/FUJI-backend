package com.zpi.fujibackend.progress.controller;

import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.KanjiAmountRemainingDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
class ProgressController {

    private final ProgressFacade progressFacade;

    private static final class Routes {
        private static final String STREAK = "/daily-streak";
        private static final String KANJI_LEARNED = "/kanji-learned";
        private static final String LEVEL = "/level";
        private static final String KANJI_REMAINING = "/kanji-remaining";
    }

    @GetMapping(Routes.STREAK)
    DailyStreakDto getDailyStreak() {
       return progressFacade.getDailyStreak();
    }

    @GetMapping(Routes.KANJI_LEARNED)
    KanjiLearnedDto getKanjiLearnedAmount() {
        return progressFacade.getKanjiLearnedAmount();
    }

    @GetMapping(Routes.LEVEL)
    UserLevelDto getUserLevel() {
        return progressFacade.getUserLevel();
    }

    @GetMapping(Routes.KANJI_REMAINING)
    KanjiAmountRemainingDto getKanjiAmountRemainingForLevel(@RequestParam int level) {
        return progressFacade.getKanjiAmountRemainingForLevel(level);
    }
}

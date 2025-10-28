package com.zpi.fujibackend.kanji.controller;


import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/kanji")
@RequiredArgsConstructor
class KanjiController {

    private final KanjiFacade kanjiFacade;

    private static final class Routes {
        private static final String LEVEL = "/level/{level}";
        private static final String DETAILS = "/{uuid}";
    }


    @GetMapping(Routes.LEVEL)
    List<KanjiDto> getKanjiByLevel(@PathVariable int level) {
        return kanjiFacade.getByLevel(level);
    }

    @GetMapping(Routes.DETAILS)
    KanjiDetailDto getKanjiByUuid(@PathVariable UUID uuid) {
        return kanjiFacade.getByUuid(uuid);
    }

}

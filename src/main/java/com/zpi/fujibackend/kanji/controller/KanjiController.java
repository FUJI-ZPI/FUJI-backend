package com.zpi.fujibackend.kanji.controller;


import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiCharacterDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/kanji")
@RequiredArgsConstructor
class KanjiController {

    private final KanjiFacade kanjiFacade;

    private static final class Routes {
        private static final String LEVEL = "/{level}";
    }


    @GetMapping(Routes.LEVEL)
    List<KanjiCharacterDto> getKanjiByLevel(@PathVariable int level) {
        return kanjiFacade.getKanjisByLevel(level);
    }

}

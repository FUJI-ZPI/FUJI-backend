package com.zpi.fujibackend.kanji.controller;


import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/kanji/v1")
@RequiredArgsConstructor
class KanjiController {

    private final KanjiFacade kanjiFacade;

    private static final class Routes {
        private static final String ROOT = "/kanji";
        private static final String LEVEL = ROOT + "/{level}";
    }


    @GetMapping(Routes.LEVEL)
    List<KanjiDto> getKanjiByLevel(@PathVariable int level) {
        return kanjiFacade.getKanjisByLevel(level);
    }

}

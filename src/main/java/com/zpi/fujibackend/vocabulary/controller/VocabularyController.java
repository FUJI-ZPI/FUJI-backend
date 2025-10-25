package com.zpi.fujibackend.vocabulary.controller;


import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vocabulary/v1")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyFacade vocabularyFacade;

    private static final class Routes {

        private static final String ROOT = "/vocabulary";
        private static final String LEVEL = ROOT + "/{level}";

    }

    @GetMapping(Routes.LEVEL)
    public List<VocabularyDto> getVocabularyByLevel(@PathVariable int level) {
        return vocabularyFacade.getVocabularyByLevel(level);
    }

}

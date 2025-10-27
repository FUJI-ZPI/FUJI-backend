package com.zpi.fujibackend.vocabulary.controller;


import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vocabulary")
@RequiredArgsConstructor
class VocabularyController {

    private final VocabularyFacade vocabularyFacade;

    private static final class Routes {

        private static final String LEVEL = "/{level}";
        private static final String DETAILS = "/details/{uuid}";

    }

    @GetMapping(Routes.LEVEL)
    List<VocabularyDto> getVocabularyByLevel(@PathVariable int level) {
        return vocabularyFacade.getByLevel(level);
    }

    @GetMapping(Routes.DETAILS)
    VocabularyDetailsDto findVocabularyByUuid(@PathVariable("uuid") UUID uuid) {
        return vocabularyFacade.getByUuid(uuid);
    }
}

package com.zpi.fujibackend.vocabulary.controller;


import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vocabulary/v1")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyFacade vocabularyFacade;

    private static final class Routes {

        private static final String ROOT = "/vocabulary";
        private static final String LEVEL = ROOT + "/{level}";
        private static final String DETAILS = ROOT + "/details/{uuid}";

    }

    @GetMapping(Routes.LEVEL)
    public List<VocabularyDto> getVocabularyByLevel(@PathVariable int level) {
        return vocabularyFacade.getVocabularyByLevel(level);
    }

    @GetMapping(Routes.DETAILS)
    public ResponseEntity<VocabularyDetailsDto> findVocabularyByUuid(@PathVariable("uuid") UUID uuid) {
        return vocabularyFacade.findVocabularyByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

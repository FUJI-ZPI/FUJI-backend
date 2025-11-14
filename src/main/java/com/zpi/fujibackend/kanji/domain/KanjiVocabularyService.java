package com.zpi.fujibackend.kanji.domain;

import com.zpi.fujibackend.kanji.KanjiVocabularyFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class KanjiVocabularyService implements KanjiVocabularyFacade {

    private final KanjiRepository kanjiRepository;

    @Override
    public List<KanjiDto> getKanjisByIds(List<Long> ids) {
        return kanjiRepository.findAllById(ids).stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }
}

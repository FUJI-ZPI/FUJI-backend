package com.zpi.fujibackend.kanji.domain;

import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class KanjiService implements KanjiFacade {

    private final KanjiRepository kanjiRepository;


    @Override
    public List<KanjiDto> getKanjisByLevel(int level) {
        return kanjiRepository.findByLevel(level)
                .stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }
}

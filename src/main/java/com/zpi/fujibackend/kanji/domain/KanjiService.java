package com.zpi.fujibackend.kanji.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonNodeConverter;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.kanji.dto.WanikaniKanjiJsonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class KanjiService implements KanjiFacade {

    private final KanjiRepository kanjiRepository;
    private final JsonNodeConverter jsonNodeConverter;


    @Override
    public List<KanjiDto> getKanjisByLevel(int level) {
        return kanjiRepository.findByLevel(level)
                .stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }

    @Override
    public KanjiDetailDto getKanjiByUuid(UUID uuid) {
        return kanjiRepository.getByUuid(uuid)
                .map(k ->
                        new KanjiDetailDto(
                                k.getUuid(),
                                k.getLevel(),
                                k.getCharacter(),
                                k.getUnicodeCharacter(),
                                jsonNodeConverter.convertToDto(k.getDocument(), WanikaniKanjiJsonDto.class)
                        )
                )
                .orElseThrow(() -> new NotFoundException("No Kanji for UUID: " + uuid));
    }
}

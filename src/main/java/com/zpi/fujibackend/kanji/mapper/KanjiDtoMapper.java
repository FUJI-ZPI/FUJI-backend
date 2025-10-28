package com.zpi.fujibackend.kanji.mapper;

import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KanjiDtoMapper {
    public KanjiDto toDto(Kanji kanji) {
        return new KanjiDto(
                kanji.getUuid(),
                kanji.getCharacter(),
                kanji.getDocument(),
                kanji.getDrawingData(),
                kanji.getSvgData()
        );
    }

    public List<KanjiDto> toDtoList(List<Kanji> kanjis) {
        return kanjis.stream().map(this::toDto).toList();
    }
}

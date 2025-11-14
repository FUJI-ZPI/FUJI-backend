package com.zpi.fujibackend.radical.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.KanjiRadicalFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.radical.RadicalFacade;
import com.zpi.fujibackend.radical.dto.RadicalDetailDto;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.radical.dto.WanikaniRadicalJsonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class RadicalService implements RadicalFacade {

    private final RadicalRepository radicalRepository;
    private final KanjiRadicalFacade kanjiRadicalFacade;

    @Override
    public List<RadicalDto> getRadicalByLevel(int level) {
        return radicalRepository.findByLevel(level).stream()
                .map(radical -> new RadicalDto(radical.getUuid(), radical.getCharacter()))
                .toList();
    }


    @Override
    public RadicalDetailDto getRadicalByUuid(UUID uuid) {
        Radical radical = radicalRepository.getByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("No Radical for UUID: " + uuid));

        WanikaniRadicalJsonDto wanikaniDto = JsonConverter.convertToDto(
                radical.getDocument(),
                WanikaniRadicalJsonDto.class
        );

        List<KanjiDto> relatedKanjis = new ArrayList<>();

        if (wanikaniDto != null && wanikaniDto.data() != null && wanikaniDto.data().amalgamationSubjectIds() != null) {
            List<Long> kanjiIds = wanikaniDto.data().amalgamationSubjectIds().stream()
                    .map(Long::valueOf)
                    .toList();

            if (!kanjiIds.isEmpty()) {
                relatedKanjis = kanjiRadicalFacade.getKanjisByIds(kanjiIds);
            }
        }

        return new RadicalDetailDto(
                radical.getLevel(),
                radical.getCharacter(),
                radical.getCharacterUnicode(),
                radical.getSlug(),
                wanikaniDto,
                relatedKanjis
        );
    }
}

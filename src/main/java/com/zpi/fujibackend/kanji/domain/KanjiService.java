package com.zpi.fujibackend.kanji.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.kanji.dto.ReferenceKanjiDto;
import com.zpi.fujibackend.kanji.dto.WanikaniKanjiJsonDto;
import com.zpi.fujibackend.radical.RadicalKanjiFacade;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.vocabulary.VocabularyKanjiFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class KanjiService implements KanjiFacade {

    private final KanjiRepository kanjiRepository;
    private final UserFacade userFacade;
    private final RadicalKanjiFacade radicalKanjiFacade;
    private final VocabularyKanjiFacade vocabularyKanjiFacade;

    @Override
    public List<KanjiDto> getByLevel(int level) {
        return kanjiRepository.findByLevel(level)
                .stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }

    @Override
    public KanjiDetailDto getByUuid(UUID uuid) {
        Kanji kanji = kanjiRepository.getByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("No Kanji for UUID: " + uuid));

        WanikaniKanjiJsonDto wanikaniDto = JsonConverter.convertToDto(kanji.getDocument(), WanikaniKanjiJsonDto.class);
        List<String> svgPath = JsonConverter.convertJsonStringToListOfString(kanji.getSvgData());

        List<RadicalDto> componentRadicals = fetchComponentRadicals(wanikaniDto);
        List<VocabularyDto> relatedVocabulary = fetchRelatedVocabulary(wanikaniDto);
        List<KanjiDto> similarKanji = fetchVisuallySimilarKanji(wanikaniDto);

        return new KanjiDetailDto(
                kanji.getUuid(),
                kanji.getLevel(),
                kanji.getCharacter(),
                kanji.getUnicodeCharacter(),
                wanikaniDto,
                svgPath,
                componentRadicals,
                relatedVocabulary,
                similarKanji
        );
    }

    private List<RadicalDto> fetchComponentRadicals(WanikaniKanjiJsonDto wanikaniDto) {
        if (wanikaniDto == null || wanikaniDto.data() == null || wanikaniDto.data().componentSubjectIds() == null) {
            return new ArrayList<>();
        }

        List<Long> radicalIds = wanikaniDto.data().componentSubjectIds().stream()
                .map(Long::valueOf)
                .toList();

        if (radicalIds.isEmpty()) {
            return new ArrayList<>();
        }

        return radicalKanjiFacade.getRadicalsByIds(radicalIds);
    }

    private List<VocabularyDto> fetchRelatedVocabulary(WanikaniKanjiJsonDto wanikaniDto) {
        if (wanikaniDto == null || wanikaniDto.data() == null || wanikaniDto.data().amalgamationSubjectIds() == null) {
            return new ArrayList<>();
        }

        List<Long> vocabIds = wanikaniDto.data().amalgamationSubjectIds().stream()
                .map(Long::valueOf)
                .toList();

        if (vocabIds.isEmpty()) {
            return new ArrayList<>();
        }

        return vocabularyKanjiFacade.getVocabularyByIds(vocabIds);
    }

    private List<KanjiDto> fetchVisuallySimilarKanji(WanikaniKanjiJsonDto wanikaniDto) {
        if (wanikaniDto == null || wanikaniDto.data() == null || wanikaniDto.data().visuallySimilarSubjectIds() == null) {
            return new ArrayList<>();
        }

        List<Long> kanjiIds = wanikaniDto.data().visuallySimilarSubjectIds().stream()
                .map(Long::valueOf)
                .toList();

        if (kanjiIds.isEmpty()) {
            return new ArrayList<>();
        }

        return kanjiRepository.findAllById(kanjiIds).stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }

    @Override
    public List<KanjiDetailDto> getKanjisNotInCards(int size) {
        return kanjiRepository.findAllNotInCardsForUser(userFacade.getCurrentUserId(), userFacade.getCurrentUserLevel(), Pageable.ofSize(size))
                .stream()
                .map(KanjiDetailDto::toDto)
                .toList();
    }

    @Override
    public Optional<Kanji> getKanjiByUuid(UUID uuid) {
        return kanjiRepository.findByUuid(uuid);
    }

    @Override
    public List<ReferenceKanjiDto> getKanjiByStrokeNumber(int strokeNumber) {
        return kanjiRepository.findByDrawingDataCount(strokeNumber).stream()
                .map(kanji -> {
                    final List<List<List<Double>>> points = parseDrawingData(kanji.getDrawingData());
                    return new ReferenceKanjiDto(kanji.getUuid(), kanji.getCharacter(), points);
                })
                .toList();
    }

    private List<List<List<Double>>> parseDrawingData(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing drawingData JSON", e);
        }
    }
}

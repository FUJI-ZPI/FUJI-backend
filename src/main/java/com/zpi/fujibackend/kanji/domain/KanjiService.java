package com.zpi.fujibackend.kanji.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.kanji.dto.ReferenceKanjiDto;
import com.zpi.fujibackend.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class KanjiService implements KanjiFacade {

    private final KanjiRepository kanjiRepository;
    private final UserFacade userFacade;

    @Override
    public List<KanjiDto> getByLevel(int level) {
        return kanjiRepository.findByLevel(level)
                .stream()
                .map(kanji -> new KanjiDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }

    @Override
    public KanjiDetailDto getByUuid(UUID uuid) {
        return kanjiRepository.getByUuid(uuid)
                .map(KanjiDetailDto::toDto)
                .orElseThrow(() -> new NotFoundException("No Kanji for UUID: " + uuid));
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
                .map(kanji ->  {
                    final List<List<List<Double>>> points = parseDrawingData(kanji.getDrawingData());
                    return new ReferenceKanjiDto(kanji.getUuid(), kanji.getCharacter(), points);
                })
                .toList();
    }

    private List<List<List<Double>>> parseDrawingData(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing drawingData JSON", e);
        }
    }
}

package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.KanjiVocabularyFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import com.zpi.fujibackend.vocabulary.dto.WanikaniVocabularyJsonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class VocabularyService implements VocabularyFacade {

    private final VocabularyRepository vocabularyRepository;
    private final KanjiVocabularyFacade kanjiVocabularyFacade;

    @Override
    public List<VocabularyDto> getByLevel(int level) {
        return vocabularyRepository.getVocabularyByLevel(level)
                .stream()
                .map(vocab -> new VocabularyDto(vocab.getUuid(), vocab.getCharacters()))
                .toList();
    }

    @Override
    public VocabularyDetailsDto getByUuid(UUID uuid) {
        Vocabulary vocabulary = vocabularyRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("No Vocabulary for UUID: " + uuid));

        WanikaniVocabularyJsonDto wanikaniDto = JsonConverter.convertToDto(
                vocabulary.getDocument(),
                WanikaniVocabularyJsonDto.class
        );

        List<KanjiDto> componentKanji = fetchComponentKanji(wanikaniDto);

        return new VocabularyDetailsDto(
                vocabulary.getLevel(),
                vocabulary.getCharacters(),
                vocabulary.getUnicodeCharacters(),
                wanikaniDto,
                componentKanji
        );
    }

    private List<KanjiDto> fetchComponentKanji(WanikaniVocabularyJsonDto wanikaniDto) {
        if (wanikaniDto == null || wanikaniDto.data() == null || wanikaniDto.data().componentSubjectIds() == null) {
            return new ArrayList<>();
        }

        List<Integer> subjectIds = wanikaniDto.data().componentSubjectIds();

        if (subjectIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> kanjiIds = subjectIds.stream()
                .map(Long::valueOf)
                .toList();

        return kanjiVocabularyFacade.getKanjisByIds(kanjiIds);
    }
}

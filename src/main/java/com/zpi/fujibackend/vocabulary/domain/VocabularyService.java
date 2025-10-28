package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonNodeConverter;
import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import com.zpi.fujibackend.vocabulary.dto.WanikaniVocabularyJsonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class VocabularyService implements VocabularyFacade {

    private final VocabularyRepository vocabularyRepository;
    private final JsonNodeConverter jsonNodeConverter;


    @Override
    public List<VocabularyDto> getByLevel(int level) {

        return vocabularyRepository.getVocabularyByLevel(level)
                .stream()
                .map(vocab -> new VocabularyDto(vocab.getUuid(), vocab.getCharacters()))
                .toList();
    }

    @Override
    public VocabularyDetailsDto getByUuid(UUID uuid) {

        return vocabularyRepository.findByUuid(uuid)
                .map(
                        v -> new VocabularyDetailsDto(
                                v.getLevel(),
                                v.getCharacters(),
                                v.getUnicodeCharacters(),
                                jsonNodeConverter.convertToDto(v.getDocument(), WanikaniVocabularyJsonDto.class)
                        ))
                .orElseThrow(() -> new NotFoundException("No Vocabulary for UUID: " + uuid));

    }
}

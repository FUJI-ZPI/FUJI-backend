package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.config.converter.JsonNodeConverter;
import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class VocabularyService implements VocabularyFacade {

    private final VocabularyRepository vocabularyRepository;
    private final JsonNodeConverter jsonNodeConverter;


    @Override
    public List<VocabularyDto> getVocabularyByLevel(int level) {
        List<Vocabulary> vocabularyList = vocabularyRepository.getVocabularyByLevel(level);

        return vocabularyList
                .stream()
                .map(vocab -> new VocabularyDto(vocab.getUuid(), vocab.getCharacters()))
                .toList();
    }

    @Override
    public Optional<VocabularyDetailsDto> findVocabularyByUuid(UUID uuid) {


        return vocabularyRepository.findByUuid(uuid)
                .map(vocab -> {
                    var jsonNode = jsonNodeConverter.toJsonNode(vocab.getDocument());

                    return new VocabularyDetailsDto(
                            vocab.getLevel(),
                            vocab.getCharacters(),
                            vocab.getUnicodeCharacters(),
                            jsonNode
                    );
                });
    }
}

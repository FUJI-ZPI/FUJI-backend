package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.vocabulary.VocabularyFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class VocabularyService implements VocabularyFacade {

    private final VocabularyRepository vocabularyRepository;

    @Override
    public List<VocabularyDto> getVocabularyByLevel(int level) {
        List<Vocabulary> vocabularyList = vocabularyRepository.getVocabularyByLevel(level);

        return vocabularyList
                .stream()
                .map(vocab -> new VocabularyDto(vocab.getUuid(), vocab.getCharacters()))
                .toList();
    }
}

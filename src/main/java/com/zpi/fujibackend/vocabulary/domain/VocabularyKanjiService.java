package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.vocabulary.VocabularyKanjiFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class VocabularyKanjiService implements VocabularyKanjiFacade {

    private final VocabularyRepository vocabularyRepository;

    @Override
    public List<VocabularyDto> getVocabularyByIds(final List<Long> ids) {
        return vocabularyRepository.findAllById(ids).stream()
                .map(vocabulary -> new VocabularyDto(vocabulary.getUuid(), vocabulary.getCharacters()))
                .toList();
    }
}

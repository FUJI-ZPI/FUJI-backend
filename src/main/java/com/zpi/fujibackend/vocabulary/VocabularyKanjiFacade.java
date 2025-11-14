package com.zpi.fujibackend.vocabulary;

import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;

import java.util.List;

public interface VocabularyKanjiFacade {

    List<VocabularyDto> getVocabularyByIds(List<Long> ids);
}

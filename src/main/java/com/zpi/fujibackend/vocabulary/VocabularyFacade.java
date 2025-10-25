package com.zpi.fujibackend.vocabulary;


import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyFacade {

    List<VocabularyDto> getVocabularyByLevel(int level);

    Optional<VocabularyDetailsDto> findVocabularyByUuid(UUID uuid);

}

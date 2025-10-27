package com.zpi.fujibackend.vocabulary;


import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;

import java.util.List;
import java.util.UUID;

public interface VocabularyFacade {

    List<VocabularyDto> getByLevel(int level);

    VocabularyDetailsDto getByUuid(UUID uuid);

}

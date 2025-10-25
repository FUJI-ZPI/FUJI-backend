package com.zpi.fujibackend.vocabulary;


import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;

import java.util.List;

public interface VocabularyFacade {

    List<VocabularyDto> getVocabularyByLevel(int level);


}

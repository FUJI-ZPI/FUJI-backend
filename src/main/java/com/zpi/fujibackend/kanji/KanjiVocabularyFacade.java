package com.zpi.fujibackend.kanji;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;

public interface KanjiVocabularyFacade {

    List<KanjiDto> getKanjisByIds(List<Long> ids);
}

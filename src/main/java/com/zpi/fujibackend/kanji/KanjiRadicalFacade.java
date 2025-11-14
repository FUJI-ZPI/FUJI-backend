package com.zpi.fujibackend.kanji;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;

public interface KanjiRadicalFacade {

    List<KanjiDto> getKanjisByIds(List<Long> ids);
}

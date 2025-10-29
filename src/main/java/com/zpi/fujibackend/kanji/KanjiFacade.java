package com.zpi.fujibackend.kanji;


import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;
import java.util.UUID;

public interface KanjiFacade {
    List<KanjiDto> getByLevel(int level);

    KanjiDetailDto getByUuid(UUID uuid);

    List<KanjiDetailDto> getKanjisNotInCards(int size);

    Kanji getKanjiByUuid(UUID uuid);
}

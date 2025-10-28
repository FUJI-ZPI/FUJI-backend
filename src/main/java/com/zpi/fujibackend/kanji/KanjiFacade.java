package com.zpi.fujibackend.kanji;


import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiCharacterDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;
import java.util.UUID;

public interface KanjiFacade {
    List<KanjiCharacterDto> getKanjisByLevel(int level);
    List<KanjiDto> getKanjisNotInCards();
    Kanji getKanjiByUuid(UUID uuid);
}

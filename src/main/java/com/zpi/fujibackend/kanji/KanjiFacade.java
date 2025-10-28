package com.zpi.fujibackend.kanji;


import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;
import java.util.UUID;

public interface KanjiFacade {

    List<KanjiDto> getKanjisByLevel(int level);

    KanjiDetailDto getKanjiByUuid(UUID uuid);

}

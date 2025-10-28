package com.zpi.fujibackend.srs;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;
import java.util.UUID;

public interface SrsFacade {
    List<KanjiDto> getReviewBatch(int size);
    List<KanjiDto> getLessonBatch(int size);

    void increaseFamiliarity(UUID uuid);
    void decreaseFamiliarity(UUID uuid);
    void addCard(UUID kanjiUuid);
}

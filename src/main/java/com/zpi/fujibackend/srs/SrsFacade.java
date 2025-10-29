package com.zpi.fujibackend.srs;

import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;

import java.util.List;
import java.util.UUID;

public interface SrsFacade {
    List<KanjiDetailDto> getReviewBatch(int size);
    List<KanjiDetailDto> getLessonBatch(int size);

    void increaseFamiliarity(UUID uuid);
    void decreaseFamiliarity(UUID uuid);
    void addCard(UUID kanjiUuid);
}

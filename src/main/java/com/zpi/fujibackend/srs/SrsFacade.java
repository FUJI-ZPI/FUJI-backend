package com.zpi.fujibackend.srs;

import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.srs.domain.Card;
import com.zpi.fujibackend.srs.dto.CardDto;

import java.util.List;
import java.util.UUID;

public interface SrsFacade {
    List<CardDto> getReviewBatch(int size);

    List<KanjiDetailDto> getLessonBatch(int size);

    Card increaseFamiliarity(UUID uuid);

    Card decreaseFamiliarity(UUID uuid);

    Card addCard(UUID kanjiUuid);

}

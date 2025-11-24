package com.zpi.fujibackend.srs;

import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.user.domain.User;

import java.util.List;
import java.util.UUID;

public interface SrsFacade {
    List<KanjiDetailDto> getReviewBatchForCurrentUser(int size);

    List<KanjiDetailDto> getReviewBatch(int size, User user);

    List<KanjiDetailDto> getLessonBatchForCurrentUser(int size);

    void increaseFamiliarity(UUID uuid);

    void decreaseFamiliarity(UUID uuid);

    Boolean addCard(UUID kanjiUuid);
}

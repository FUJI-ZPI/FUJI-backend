package com.zpi.fujibackend.srs.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.srs.dto.CardDto;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
class SrsService implements SrsFacade {

    private static final int[] INTERVALS_IN_HOURS = {0, 1, 2, 4, 8, 24, 48, 168, 336, 672, 2688};
    private static final int LEARNING_THRESHOLD = INTERVALS_IN_HOURS.length / 2;

    private final CardRepository cardRepository;
    private final KanjiFacade kanjiFacade;
    private final UserFacade userFacade;
    private final ProgressFacade progressFacade;

    @Override
    public List<CardDto> getReviewBatchForCurrentUser(int size) {
        User currentUser = userFacade.getCurrentUser();
        return getReviewBatch(size, currentUser);
    }

    @Override
    public List<CardDto> getReviewBatch(int size, User user) {
        return cardRepository.findDueForUser(user.getId(), Instant.now(), PageRequest.of(0, size))
                .stream()
                .map(card -> new CardDto(card.getUuid(), KanjiDetailDto.toDto(card.getKanji())))
                .toList();
    }

    @Override
    public List<KanjiDetailDto> getLessonBatchForCurrentUser(int size) {
        return kanjiFacade.getKanjisNotInCardsforUser(
                userFacade.getCurrentUserId(),
                progressFacade.getUserLevel().level(),
                size
        );
    }

    @Override
    @Transactional
    public Card increaseFamiliarity(UUID kanjiUuid) {
        User user = userFacade.getCurrentUser();
        Kanji kanji = kanjiFacade.getKanjiByUuid(kanjiUuid);
        Card card = cardRepository.findByUserAndKanji(user, kanji)
                .orElseThrow(() -> new NotFoundException("No Card found"));
        return changeFamiliarity(card, Math.min(card.getFamiliarity() + 1, INTERVALS_IN_HOURS.length - 1));
    }

    @Override
    @Transactional
    public Card decreaseFamiliarity(UUID kanjiUuid) {
        User user = userFacade.getCurrentUser();
        Kanji kanji = kanjiFacade.getKanjiByUuid(kanjiUuid);
        Card card = cardRepository.findByUserAndKanji(user, kanji)
                .orElseThrow(() -> new NotFoundException("No Card found"));
        return changeFamiliarity(card, Math.max(card.getFamiliarity() - 1, 0));
    }

    private Card changeFamiliarity(Card card, int newFamiliarity) {
        int oldFamiliarity = card.getFamiliarity();
        card.setFamiliarity(newFamiliarity);
        card.setIntervalHours(INTERVALS_IN_HOURS[newFamiliarity]);
        card.setLastReviewed(Instant.now());
        card.setNextDue(Instant.now().plus(INTERVALS_IN_HOURS[newFamiliarity], ChronoUnit.HOURS));

        if (newFamiliarity >= LEARNING_THRESHOLD && oldFamiliarity < LEARNING_THRESHOLD) {
            progressFacade.markKanjiAsLearned(card.getKanji());
        }

        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public Card addCard(UUID kanjiUuid) {
        Kanji kanji = kanjiFacade.getKanjiByUuid(kanjiUuid);
        User user = userFacade.getCurrentUser();
        Card card = new Card(
                kanji,
                user,
                0,
                INTERVALS_IN_HOURS[0],
                Instant.now(),
                Instant.now().plus(INTERVALS_IN_HOURS[0], ChronoUnit.HOURS)
        );
        return cardRepository.save(card);
    }
}
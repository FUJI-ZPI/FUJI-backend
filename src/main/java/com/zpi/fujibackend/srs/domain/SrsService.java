package com.zpi.fujibackend.srs.domain;

import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class SrsService implements SrsFacade {
    private final CardRepository cardRepository;

    private final KanjiFacade kanjiFacade;
    private final UserFacade userFacade;

    private final int[] intervals = {4, 8, 24, 48, 168, 336, 672, 2688};

    @Override
    public List<KanjiDetailDto> getReviewBatch(int size) {
        User currentUser = userFacade.getCurrentUser();
        return cardRepository.findDueForUser(currentUser.getId(), Instant.now(), PageRequest.of(0, size))
                .stream()
                .map(card -> KanjiDetailDto.toDto(card.getKanji()))
                .toList();
    }

    @Override
    public List<KanjiDetailDto> getLessonBatch(int size) {
        return kanjiFacade.getKanjisNotInCards();
    }

    @Override
    public void increaseFamiliarity(UUID uuid) {
        Card card = cardRepository.findByUuid(uuid);
        changeFamiliarity(card, Math.min(card.getFamiliarity() + 1, intervals.length - 1));
    }

    @Override
    public void decreaseFamiliarity(UUID uuid) {
        Card card = cardRepository.findByUuid(uuid);
        changeFamiliarity(card, Math.max(card.getFamiliarity() - 1, 0));
    }

    private void changeFamiliarity(Card card, int newFamiliarity) {
        card.setFamiliarity(newFamiliarity);
        card.setIntervalHours(intervals[newFamiliarity]);
        card.setLastReviewed(Instant.now());
        card.setNextDue(Instant.now().plus(intervals[newFamiliarity], ChronoUnit.HOURS));
    }

    @Override
    public void addCard(UUID kanjiUuid) {
        Card card = new Card(
                kanjiFacade.getKanjiByUuid(kanjiUuid),
                userFacade.getCurrentUser(),
                0,
                intervals[0],
                Instant.now(),
                Instant.now().plus(intervals[0], ChronoUnit.HOURS)
        );
        cardRepository.save(card);
    }
}

package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.KanjiRemainingDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ProgressService implements ProgressFacade {

    private final ProgressRepository progressRepository;
    private final UserFacade userFacade;
    private final KanjiFacade kanjiFacade;

    @Override
    public UserLevelDto getUserLevel() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(Progress::getLevel)
                .map(UserLevelDto::new)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getUuid()));
    }

    @Override
    @Transactional
    public DailyStreakDto getDailyStreak() {
        final User user = userFacade.getCurrentUser();
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found"));

        ZoneId zone = ZoneOffset.UTC;
        LocalDate today = LocalDate.now(zone);

        LocalDate lastUpdateDate = progress.getLastStreakUpdated() != null
                ? progress.getLastStreakUpdated().atZone(zone).toLocalDate()
                : null;

        int currentEffectiveStreak = progress.getDailyStreak();

        if (lastUpdateDate == null || lastUpdateDate.isBefore(today.minusDays(1))) {
            currentEffectiveStreak = 0;

            if (progress.getDailyStreak() != 0) {
                progress.setDailyStreak(0);
                progressRepository.save(progress);
            }
        }

        return new DailyStreakDto(currentEffectiveStreak);
    }

    @Override
    @Transactional
    public void updateDailyStreak(User user, Instant activityTimestamp) {
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found"));

        ZoneId zone = ZoneOffset.UTC;
        LocalDate activityDate = activityTimestamp.atZone(zone).toLocalDate();
        LocalDate lastUpdateDate = progress.getLastStreakUpdated() != null
                ? progress.getLastStreakUpdated().atZone(zone).toLocalDate()
                : null;

        if (lastUpdateDate != null) {
            if (lastUpdateDate.isEqual(activityDate.minusDays(1))) {
                progress.setDailyStreak(progress.getDailyStreak() + 1);
            } else if (!lastUpdateDate.isEqual(activityDate)){
                progress.setDailyStreak(1);
            }
        } else {
            progress.setDailyStreak(1);
        }

        if (progress.getDailyStreak() > progress.getMaxDailyStreak()) {
            progress.setMaxDailyStreak(progress.getDailyStreak());
        }

        progress.setLastStreakUpdated(activityTimestamp);
        progressRepository.save(progress);
    }

    @Override
    public KanjiLearnedDto getKanjiLearnedAmount() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(progress -> new KanjiLearnedDto(progress.getLearnedKanji().size()))
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));
    }

    @Override
    public KanjiRemainingDto getKanjiRemainingForLevel(int level) {
        final User user = userFacade.getCurrentUser();
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));

        Set<UUID> learnedKanjiIds = progress.getLearnedKanji().stream()
                .map(Kanji::getUuid)
                .collect(Collectors.toSet());

        List<KanjiDto> allMissingKanji = new ArrayList<>();

        for (int i = 1; i <= level; i++) {
            List<KanjiDto> kanjiOnLevel = kanjiFacade.getByLevel(i);

            List<KanjiDto> missingOnThisLevel = kanjiOnLevel.stream()
                    .filter(dto -> !learnedKanjiIds.contains(dto.uuid()))
                    .toList();

            allMissingKanji.addAll(missingOnThisLevel);
        }

        return new KanjiRemainingDto(allMissingKanji.size(), allMissingKanji);
    }


    @Override
    @Transactional
    public void markKanjiAsLearned(Kanji kanji) {
        User user = userFacade.getCurrentUser();
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getUuid()));

        progress.getLearnedKanji().add(kanji);

        progressRepository.save(progress);

        Integer currentLevel = progress.getLevel();

        long learnedKanjiAtCurrentLevel = progress.getLearnedKanji().stream()
                .filter(k -> k.getLevel().equals(currentLevel))
                .count();

        int totalKanjiInLevel = kanjiFacade.getByLevel(currentLevel).size();

        if (totalKanjiInLevel > 0 && learnedKanjiAtCurrentLevel >= totalKanjiInLevel) {
            progress.setLevel(progress.getLevel() + 1);
            progressRepository.save(progress);
        }
    }
}

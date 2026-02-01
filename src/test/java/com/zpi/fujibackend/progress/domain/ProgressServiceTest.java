package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.progress.dto.DailyStreakDto;
import com.zpi.fujibackend.progress.dto.KanjiLearnedDto;
import com.zpi.fujibackend.progress.dto.KanjiAmountRemainingDto;
import com.zpi.fujibackend.progress.dto.UserLevelDto;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private UserFacade userFacade;

    @Mock
    private KanjiFacade kanjiFacade;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void getUserLevel_ShouldReturnLevelDto() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        Progress progress = new Progress();
        progress.setLevel(5);

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        UserLevelDto result = progressService.getUserLevel();

        assertThat(result.level()).isEqualTo(5);
    }

    @Test
    void getUserLevel_ShouldThrowException_WhenProgressNotFound() {
        User user = new User();
        user.setUuid(UUID.randomUUID());

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.getUserLevel())
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDailyStreak_ShouldReturnCurrentStreak_WhenLastUpdateWasToday() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(10);
        progress.setLastStreakUpdated(Instant.now());

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        DailyStreakDto result = progressService.getDailyStreak();

        assertThat(result.streak()).isEqualTo(10);
    }

    @Test
    void getDailyStreak_ShouldReturnZero_WhenLastUpdateWasTwoDaysAgo() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(10);

        Instant twoDaysAgo = LocalDate.now().minusDays(2).atStartOfDay(ZoneOffset.UTC).toInstant();
        progress.setLastStreakUpdated(twoDaysAgo);

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        DailyStreakDto result = progressService.getDailyStreak();

        assertThat(result.streak()).isEqualTo(0);
        verify(progressRepository).save(progress);
    }
    @Test
    void updateDailyStreak_ShouldIncrementStreak_WhenLastUpdateWasYesterday() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(5);
        progress.setMaxDailyStreak(10);

        Instant yesterday = LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        progress.setLastStreakUpdated(yesterday);

        Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        progressService.updateDailyStreak(user, today);

        assertThat(progress.getDailyStreak()).isEqualTo(6);
        assertThat(progress.getLastStreakUpdated()).isEqualTo(today);
        verify(progressRepository).save(progress);
    }

    @Test
    void updateDailyStreak_ShouldResetStreakToOne_WhenLastUpdateWasTwoDaysAgo() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(5);
        progress.setMaxDailyStreak(0);

        Instant twoDaysAgo = LocalDate.now().minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC);
        progress.setLastStreakUpdated(twoDaysAgo);

        Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        progressService.updateDailyStreak(user, today);

        assertThat(progress.getDailyStreak()).isEqualTo(1);
        verify(progressRepository).save(progress);
    }

    @Test
    void updateDailyStreak_ShouldNotChangeStreak_WhenLastUpdateWasToday() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(5);
        progress.setMaxDailyStreak(0);

        Instant earlierToday = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
        progress.setLastStreakUpdated(earlierToday);

        Instant laterToday = earlierToday.plusSeconds(3600);

        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        progressService.updateDailyStreak(user, laterToday);

        assertThat(progress.getDailyStreak()).isEqualTo(5);
        assertThat(progress.getLastStreakUpdated()).isEqualTo(laterToday);
        verify(progressRepository).save(progress);
    }

    @Test
    void updateDailyStreak_ShouldInitializeStreak_WhenLastUpdateIsNull() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(0);
        progress.setMaxDailyStreak(0);
        progress.setLastStreakUpdated(null);

        Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        progressService.updateDailyStreak(user, today);

        assertThat(progress.getDailyStreak()).isEqualTo(1);
        assertThat(progress.getLastStreakUpdated()).isEqualTo(today);
        verify(progressRepository).save(progress);
    }

    @Test
    void updateDailyStreak_ShouldUpdateMaxStreak_WhenCurrentExceedsMax() {
        User user = new User();
        Progress progress = new Progress();
        progress.setDailyStreak(9);
        progress.setMaxDailyStreak(9);
        progress.setLastStreakUpdated(LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

        Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        progressService.updateDailyStreak(user, today);

        assertThat(progress.getDailyStreak()).isEqualTo(10);
        assertThat(progress.getMaxDailyStreak()).isEqualTo(10);
    }

    @Test
    void getKanjiLearnedAmount_ShouldReturnCount() {
        User user = new User();
        Progress progress = new Progress();
        progress.setLearnedKanji(new HashSet<>(List.of(new Kanji(), new Kanji())));

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));

        KanjiLearnedDto result = progressService.getKanjiLearnedAmount();

        assertThat(result.amount()).isEqualTo(2);
    }

    @Test
    void getKanjiRemainingForLevel_ShouldReturnMissingKanji() {
        User user = new User();
        Progress progress = new Progress();

        Kanji learnedKanji = new Kanji();
        learnedKanji.setUuid(UUID.randomUUID());
        progress.setLearnedKanji(Set.of(learnedKanji));

        KanjiDto learnedKanjiDto = new KanjiDto(learnedKanji.getUuid(), "A");
        KanjiDto missingKanjiDto1 = new KanjiDto(UUID.randomUUID(), "B");
        KanjiDto missingKanjiDto2 = new KanjiDto(UUID.randomUUID(), "C");

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));
        given(kanjiFacade.getByLevel(1)).willReturn(List.of(learnedKanjiDto, missingKanjiDto1));
        given(kanjiFacade.getByLevel(2)).willReturn(List.of(missingKanjiDto2));

        KanjiAmountRemainingDto result = progressService.getKanjiAmountRemainingForLevel(2);

        assertThat(result.amount()).isEqualTo(2);
    }

    @Test
    void markKanjiAsLearned_ShouldAddKanjiAndNotLevelUp_WhenNotAllKanjiLearned() {
        User user = new User();
        Progress progress = new Progress();
        progress.setLevel(1);
        progress.setLearnedKanji(new HashSet<>());

        Kanji kanji = new Kanji();
        kanji.setUuid(UUID.randomUUID());
        kanji.setLevel(1);

        KanjiDto kanjiDto1 = new KanjiDto(kanji.getUuid(), "A");
        KanjiDto kanjiDto2 = new KanjiDto(UUID.randomUUID(), "B");

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));
        given(kanjiFacade.getByLevel(1)).willReturn(List.of(kanjiDto1, kanjiDto2));

        progressService.markKanjiAsLearned(kanji);

        assertThat(progress.getLearnedKanji()).contains(kanji);
        assertThat(progress.getLevel()).isEqualTo(1);
        verify(progressRepository, times(1)).save(progress);
    }

    @Test
    void markKanjiAsLearned_ShouldLevelUp_WhenAllKanjiLearned() {
        User user = new User();
        Progress progress = new Progress();
        progress.setLevel(1);
        progress.setLearnedKanji(new HashSet<>());

        Kanji kanji = new Kanji();
        kanji.setUuid(UUID.randomUUID());
        kanji.setLevel(1);

        KanjiDto kanjiDto = new KanjiDto(kanji.getUuid(), "A");

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.of(progress));
        given(kanjiFacade.getByLevel(1)).willReturn(List.of(kanjiDto));

        progressService.markKanjiAsLearned(kanji);

        assertThat(progress.getLearnedKanji()).contains(kanji);
        assertThat(progress.getLevel()).isEqualTo(2);
        verify(progressRepository, times(2)).save(progress);
    }

    @Test
    void markKanjiAsLearned_ShouldThrowException_WhenProgressNotFound() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        Kanji kanji = new Kanji();

        given(userFacade.getCurrentUser()).willReturn(user);
        given(progressRepository.findProgressByUser(user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> progressService.markKanjiAsLearned(kanji))
                .isInstanceOf(NotFoundException.class);
    }
}
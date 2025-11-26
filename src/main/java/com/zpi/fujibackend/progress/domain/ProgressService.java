package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.progress.ProgressFacade;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
class ProgressService implements ProgressFacade {
    private final ProgressRepository progressRepository;
    private final UserFacade userFacade;
    private final SrsFacade srsFacade;

    @Override
    public void increaseUserLevel() {
        final User user = userFacade.getCurrentUser();
       Progress progress = progressRepository.findProgressByUser(user)
               .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));
       progress.setLevel(progress.getLevel() + 1);
       progressRepository.save(progress);
    }

    @Override
    public Integer getDailyStreak() {
        final User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(Progress::getDailyStreak)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));
    }

    @Transactional
    @Override
    public void updateDailyStreak(User user, Instant activityTimestamp) {
        Progress progress = progressRepository.findProgressByUser(user)
                .orElseThrow(() -> new NotFoundException("Progress not found"));

        ZoneId zone = ZoneOffset.UTC;
        LocalDate activityDate = activityTimestamp.atZone(zone).toLocalDate();
        LocalDate lastUpdateDate = progress.getStreakUpdated() != null
                ? progress.getStreakUpdated().atZone(zone).toLocalDate()
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

        progress.setStreakUpdated(activityTimestamp);
        progressRepository.save(progress);
    }

    @Override
    public long getKanjiLearnedAmount() {
        return srsFacade.countMaxFamiliarityCards();
    }
}

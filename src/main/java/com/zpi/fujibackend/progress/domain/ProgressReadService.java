package com.zpi.fujibackend.progress.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.progress.ProgressReadFacade;
import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressReadService implements ProgressReadFacade {
    private final ProgressRepository progressRepository;
    private final UserFacade userFacade;

    @Override
    public int getCurrentUserLevel() {
        User user = userFacade.getCurrentUser();
        return progressRepository.findProgressByUser(user)
                .map(Progress::getLevel)
                .orElseThrow(() -> new NotFoundException("Progress not found for user: " + user.getId()));
    }
}

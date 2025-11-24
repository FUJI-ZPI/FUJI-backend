package com.zpi.fujibackend.checker;

import com.zpi.fujibackend.algorithm.KanjiAccuracy;
import com.zpi.fujibackend.checker.dto.CheckKanjiForm;
import com.zpi.fujibackend.checker.dto.CheckStrokeForm;

public interface CheckerFacade {

    KanjiAccuracy.KanjiAccuracyResult checkKanji(CheckKanjiForm form);

    KanjiAccuracy.KanjiAccuracyResult checkStroke(CheckStrokeForm form);
}

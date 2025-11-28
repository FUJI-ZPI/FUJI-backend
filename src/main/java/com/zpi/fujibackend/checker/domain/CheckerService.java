package com.zpi.fujibackend.checker.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpi.fujibackend.activity.ActivityFacade;
import com.zpi.fujibackend.activity.dto.ActivityForm;
import com.zpi.fujibackend.activity.dto.ActivityType;
import com.zpi.fujibackend.algorithm.KanjiAccuracy;
import com.zpi.fujibackend.checker.CheckerFacade;
import com.zpi.fujibackend.checker.dto.CheckKanjiForm;
import com.zpi.fujibackend.checker.dto.CheckStrokeForm;
import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.domain.Kanji;
import com.zpi.fujibackend.srs.SrsFacade;
import com.zpi.fujibackend.srs.domain.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CheckerService implements CheckerFacade {

    private static final double PASSING_ACCURACY_THRESHOLD = 70.0;

    private final SrsFacade srsFacade;
    private final KanjiFacade kanjiFacade;
    private final ActivityFacade activityFacade;

    @Override
    public KanjiAccuracy.KanjiAccuracyResult checkKanji(CheckKanjiForm form) {
        Kanji kanji = kanjiFacade.getKanjiByUuid(form.kaniUuid());

//        KanjiAccuracy.KanjiAccuracyResult accuracyResult = calculateResult(kanji, form.userStrokes());

        final KanjiAccuracy.KanjiAccuracyResult accuracyResult = KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(form.userStrokes(), form.referenceStrokes());
        boolean isSuccess = accuracyResult.overallAccuracy() * 100 > PASSING_ACCURACY_THRESHOLD;

        if (form.isLearningSession() && !isSuccess) {
            return new KanjiAccuracy.KanjiAccuracyResult(0, List.of(0d));
        }

        Card card = updateSrsState(kanji.getUuid(), form.isLearningSession(), isSuccess);
        if (card != null) {
            saveActivity(card, form, accuracyResult, isSuccess);
        }

        return accuracyResult;
    }

    private Card updateSrsState(UUID kanjiUuid, boolean isLearningSession, boolean isSuccess) {
        if (isLearningSession && isSuccess) {
            return srsFacade.addCard(kanjiUuid);
        } else if (isLearningSession) {
            return null;
        }
        return isSuccess
                ? srsFacade.increaseFamiliarity(kanjiUuid)
                : srsFacade.decreaseFamiliarity(kanjiUuid);
    }

    private void saveActivity(Card card, CheckKanjiForm form, KanjiAccuracy.KanjiAccuracyResult result, boolean isSuccess) {
        ActivityType type = form.isLearningSession() ? ActivityType.LESSON : ActivityType.REVIEW;

        ActivityForm activityForm = new ActivityForm(
                card,
                type,
                form.userStrokes(),
                result.strokeAccuracies(),
                result.overallAccuracy(),
                isSuccess
        );

        activityFacade.addActivity(activityForm);
    }

//    private KanjiAccuracy.KanjiAccuracyResult calculateResult(Kanji kanji, List<List<List<Double>>> userStrokes) {
//        return KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(
//                userStrokes,
////                KanjiNormalizer.momentNormalizeFixed(parseDrawingData(kanji.getDrawingData()))
//        );
//    }

    @Override
    public KanjiAccuracy.KanjiAccuracyResult checkStroke(CheckStrokeForm form) {
        List<List<List<Double>>> userStrokeList = List.of(form.userStroke());
        List<List<List<Double>>> referenceStrokeList = List.of(form.referenceStroke());
        return KanjiAccuracy.KanjiComparator.calculateKanjiAccuracy(userStrokeList, referenceStrokeList);
    }

    private static List<List<List<Double>>> parseDrawingData(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing drawingData JSON", e);
        }
    }
}

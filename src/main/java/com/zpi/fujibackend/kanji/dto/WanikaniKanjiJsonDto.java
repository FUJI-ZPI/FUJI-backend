package com.zpi.fujibackend.kanji.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WanikaniKanjiJsonDto(
        int id,
        String url,
        Data data,
        String object
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Data(
            String slug,
            Integer level,
            List<Meaning> meanings,
            List<Reading> readings,
            String hiddenAt,
            String characters,
            String documentUrl,
            String meaningHint,
            String readingHint,
            Integer lessonPosition,
            String meaningMnemonic,
            String readingMnemonic,
            String unicodeCharacter,
            List<AuxiliaryMeaning> auxiliaryMeanings,
            List<Integer> componentSubjectIds,
            List<Integer> amalgamationSubjectIds,
            Integer spacedRepetitionSystemId,
            List<Integer> visuallySimilarSubjectIds
    ) {
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Meaning(
            String meaning,
            Boolean primary,
            Boolean acceptedAnswer
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Reading(
            String type,
            Boolean primary,
            String reading,
            Boolean acceptedAnswer
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record AuxiliaryMeaning(
            String type,
            String meaning
    ) {
    }
}

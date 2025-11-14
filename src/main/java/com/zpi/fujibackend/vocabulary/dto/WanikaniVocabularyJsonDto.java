package com.zpi.fujibackend.vocabulary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WanikaniVocabularyJsonDto(
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
            Integer lessonPosition,
            List<String> partsOfSpeech,
            String meaningMnemonic,
            String readingMnemonic,
            List<ContextSentence> contextSentences,
            String unicodeCharacter,
            List<AuxiliaryMeaning> auxiliaryMeanings,
            List<PronunciationAudio> pronunciationAudios,
            List<Integer> componentSubjectIds,
            Integer spacedRepetitionSystemId
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
            Boolean primary,
            String reading,
            Boolean acceptedAnswer
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record ContextSentence(
            String en,
            String ja
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record AuxiliaryMeaning(
            String type,
            String meaning
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record PronunciationAudio(
            String url,
            Metadata metadata,
            String contentType,
            String localFilename
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Metadata(
            String gender,
            Integer sourceId,
            String pronunciation,
            Integer voiceActorId,
            String voiceActorName,
            String voiceDescription
    ) {
    }
}

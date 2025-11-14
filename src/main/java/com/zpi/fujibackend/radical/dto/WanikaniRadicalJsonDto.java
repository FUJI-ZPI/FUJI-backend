package com.zpi.fujibackend.radical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WanikaniRadicalJsonDto(
        int id,
        String url,
        Data data,
        String object,
        String dataUpdatedAt
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Data(
            String slug,
            Integer level,
            List<Meaning> meanings,
            String hiddenAt,
            String characters,
            String createdAt,
            String documentUrl,
            Integer lessonPosition,
            List<CharacterImage> characterImages,
            String meaningMnemonic,
            List<AuxiliaryMeaning> auxiliaryMeanings,
            List<Integer> amalgamationSubjectIds,
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
    record AuxiliaryMeaning(
            String type,
            String meaning
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record CharacterImage(
            String url,
            Metadata metadata,
            String contentType
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Metadata(
            Boolean inlineStyles,
            String color,
            String dimensions,
            String styleName
    ) {
    }
}

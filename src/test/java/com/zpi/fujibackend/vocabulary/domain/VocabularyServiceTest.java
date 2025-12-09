package com.zpi.fujibackend.vocabulary.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.KanjiVocabularyFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDetailsDto;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import com.zpi.fujibackend.vocabulary.dto.WanikaniVocabularyJsonDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private KanjiVocabularyFacade kanjiVocabularyFacade;

    @InjectMocks
    private VocabularyService vocabularyService;

    @Test
    void getByLevel_ShouldReturnListOfVocabularyDto() {
        int level = 3;
        UUID uuid = UUID.randomUUID();
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setUuid(uuid);
        vocabulary.setCharacters("日本");

        given(vocabularyRepository.getVocabularyByLevel(level)).willReturn(List.of(vocabulary));

        List<VocabularyDto> result = vocabularyService.getByLevel(level);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().uuid()).isEqualTo(uuid);
        assertThat(result.getFirst().characters()).isEqualTo("日本");
    }

    @Test
    void getByLevel_ShouldReturnEmptyList_WhenNoVocabularyFound() {
        int level = 10;
        given(vocabularyRepository.getVocabularyByLevel(level)).willReturn(Collections.emptyList());

        List<VocabularyDto> result = vocabularyService.getByLevel(level);

        assertThat(result).isEmpty();
    }

    @Test
    void getByUuid_ShouldReturnDetailsWithComponents_WhenJsonAndIdsAreValid() {
        UUID uuid = UUID.randomUUID();
        String jsonDocument = "{}";

        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setUuid(uuid);
        vocabulary.setLevel(5);
        vocabulary.setCharacters("勉強");
        vocabulary.setUnicodeCharacters(List.of("..."));
        vocabulary.setDocument(jsonDocument);

        WanikaniVocabularyJsonDto.Data mockData = mock(WanikaniVocabularyJsonDto.Data.class);
        WanikaniVocabularyJsonDto mockJsonDto = mock(WanikaniVocabularyJsonDto.class);

        given(mockJsonDto.data()).willReturn(mockData);
        given(mockData.componentSubjectIds()).willReturn(List.of(100, 200));

        List<KanjiDto> componentKanjis = List.of(new KanjiDto(UUID.randomUUID(), "勉"));
        given(kanjiVocabularyFacade.getKanjisByIds(List.of(100L, 200L))).willReturn(componentKanjis);

        given(vocabularyRepository.findByUuid(uuid)).willReturn(Optional.of(vocabulary));

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(jsonDocument, WanikaniVocabularyJsonDto.class))
                    .thenReturn(mockJsonDto);

            VocabularyDetailsDto result = vocabularyService.getByUuid(uuid);

            assertThat(result.characters()).isEqualTo("勉強");
            assertThat(result.details()).isEqualTo(mockJsonDto);
            assertThat(result.componentKanji()).isEqualTo(componentKanjis);
        }
    }

    @Test
    void getByUuid_ShouldReturnDetailsWithEmptyComponents_WhenIdsListIsEmpty() {
        UUID uuid = UUID.randomUUID();
        String jsonDocument = "{}";

        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setUuid(uuid);
        vocabulary.setDocument(jsonDocument);

        WanikaniVocabularyJsonDto.Data mockData = mock(WanikaniVocabularyJsonDto.Data.class);
        WanikaniVocabularyJsonDto mockJsonDto = mock(WanikaniVocabularyJsonDto.class);

        given(mockJsonDto.data()).willReturn(mockData);
        given(mockData.componentSubjectIds()).willReturn(Collections.emptyList());

        given(vocabularyRepository.findByUuid(uuid)).willReturn(Optional.of(vocabulary));

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(jsonDocument, WanikaniVocabularyJsonDto.class))
                    .thenReturn(mockJsonDto);

            VocabularyDetailsDto result = vocabularyService.getByUuid(uuid);

            assertThat(result.componentKanji()).isEmpty();
            verify(kanjiVocabularyFacade, never()).getKanjisByIds(anyList());
        }
    }

    @Test
    void getByUuid_ShouldReturnDetailsWithEmptyComponents_WhenJsonIsNull() {
        UUID uuid = UUID.randomUUID();
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setUuid(uuid);
        vocabulary.setDocument(null);

        given(vocabularyRepository.findByUuid(uuid)).willReturn(Optional.of(vocabulary));

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(null, WanikaniVocabularyJsonDto.class))
                    .thenReturn(null);

            VocabularyDetailsDto result = vocabularyService.getByUuid(uuid);

            assertThat(result.details()).isNull();
            assertThat(result.componentKanji()).isEmpty();
            verify(kanjiVocabularyFacade, never()).getKanjisByIds(anyList());
        }
    }

    @Test
    void getByUuid_ShouldThrowException_WhenVocabularyNotFound() {
        UUID uuid = UUID.randomUUID();
        given(vocabularyRepository.findByUuid(uuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> vocabularyService.getByUuid(uuid))
                .isInstanceOf(NotFoundException.class);
    }
}
package com.zpi.fujibackend.kanji.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.dto.KanjiDetailDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.kanji.dto.ReferenceKanjiDto;
import com.zpi.fujibackend.kanji.dto.WanikaniKanjiJsonDto;
import com.zpi.fujibackend.radical.RadicalKanjiFacade;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.vocabulary.VocabularyKanjiFacade;
import com.zpi.fujibackend.vocabulary.dto.VocabularyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class KanjiServiceTest {

    @Mock
    private KanjiRepository kanjiRepository;

    @Mock
    private RadicalKanjiFacade radicalKanjiFacade;

    @Mock
    private VocabularyKanjiFacade vocabularyKanjiFacade;

    @InjectMocks
    private KanjiService kanjiService;

    @Test
    void getByLevel_ShouldReturnListOfKanjiDto() {
        int level = 5;
        UUID uuid = UUID.randomUUID();
        Kanji kanji = new Kanji();
        kanji.setUuid(uuid);
        kanji.setCharacter("火");

        given(kanjiRepository.findByLevel(level)).willReturn(List.of(kanji));

        List<KanjiDto> result = kanjiService.getByLevel(level);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().uuid()).isEqualTo(uuid);
        assertThat(result.getFirst().character()).isEqualTo("火");
    }

    @Test
    void getByUuid_ShouldReturnDetailDtoWithAllAssociations() {
        UUID uuid = UUID.randomUUID();
        String jsonDocument = "{}";
        String svgJson = "[]";

        Kanji kanji = new Kanji();
        kanji.setUuid(uuid);
        kanji.setLevel(5);
        kanji.setCharacter("水");
        kanji.setUnicodeCharacter("U+6C34");
        kanji.setDocument(jsonDocument);
        kanji.setSvgData(svgJson);

        WanikaniKanjiJsonDto mockDto = mock(WanikaniKanjiJsonDto.class);
        WanikaniKanjiJsonDto.Data mockData = mock(WanikaniKanjiJsonDto.Data.class);

        given(kanjiRepository.getByUuid(uuid)).willReturn(Optional.of(kanji));
        given(mockDto.data()).willReturn(mockData);

        given(mockData.componentSubjectIds()).willReturn(List.of(10));
        given(mockData.amalgamationSubjectIds()).willReturn(List.of(20));
        given(mockData.visuallySimilarSubjectIds()).willReturn(List.of(30));

        List<RadicalDto> radicals = List.of(new RadicalDto(UUID.randomUUID(), "R"));
        given(radicalKanjiFacade.getRadicalsByIds(List.of(10L))).willReturn(radicals);

        List<VocabularyDto> vocabularies = List.of(new VocabularyDto(UUID.randomUUID(), "V"));
        given(vocabularyKanjiFacade.getVocabularyByIds(List.of(20L))).willReturn(vocabularies);

        Kanji similarKanjiEntity = new Kanji();
        similarKanjiEntity.setUuid(UUID.randomUUID());
        similarKanjiEntity.setCharacter("氷");
        given(kanjiRepository.findAllById(List.of(30L))).willReturn(List.of(similarKanjiEntity));

        try (MockedStatic<JsonConverter> jsonConverter = mockStatic(JsonConverter.class)) {
            jsonConverter.when(() -> JsonConverter.convertToDto(jsonDocument, WanikaniKanjiJsonDto.class))
                    .thenReturn(mockDto);
            jsonConverter.when(() -> JsonConverter.convertJsonStringToListOfString(svgJson))
                    .thenReturn(List.of("M10 10"));

            KanjiDetailDto result = kanjiService.getByUuid(uuid);

            assertThat(result.uuid()).isEqualTo(uuid);
            assertThat(result.componentRadicals()).isEqualTo(radicals);
            assertThat(result.relatedVocabulary()).isEqualTo(vocabularies);
            assertThat(result.visuallySimilarKanji()).hasSize(1);
            assertThat(result.visuallySimilarKanji().getFirst().character()).isEqualTo("氷");
            assertThat(result.svgPath()).contains("M10 10");
        }
    }

    @Test
    void getByUuid_ShouldThrowException_WhenKanjiNotFound() {
        UUID uuid = UUID.randomUUID();
        given(kanjiRepository.getByUuid(uuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> kanjiService.getByUuid(uuid))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(uuid.toString());
    }

    @Test
    void getKanjisNotInCardsforUser_ShouldReturnMappedDtos() {
        Long userId = 1L;
        Integer userLevel = 10;
        int size = 5;

        Kanji kanji = new Kanji();
        kanji.setUuid(UUID.randomUUID());
        kanji.setCharacter("A");
        kanji.setLevel(1);
        kanji.setUnicodeCharacter("U+0041");

        given(kanjiRepository.findAllNotInCardsForUser(eq(userId), eq(userLevel), any(Pageable.class)))
                .willReturn(List.of(kanji));

        try (MockedStatic<KanjiDetailDto> dtoStatic = mockStatic(KanjiDetailDto.class)) {
            KanjiDetailDto expectedDto = new KanjiDetailDto(
                    kanji.getUuid(), 1, "A", "U+0041", null, null, null, null, null, null
            );

            dtoStatic.when(() -> KanjiDetailDto.toDto(kanji)).thenReturn(expectedDto);

            List<KanjiDetailDto> result = kanjiService.getKanjisNotInCardsforUser(userId, userLevel, size);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(expectedDto);
        }
    }

    @Test
    void getKanjiByUuid_ShouldReturnEntity() {
        UUID uuid = UUID.randomUUID();
        Kanji kanji = new Kanji();
        given(kanjiRepository.findByUuid(uuid)).willReturn(Optional.of(kanji));

        Kanji result = kanjiService.getKanjiByUuid(uuid);

        assertThat(result).isEqualTo(kanji);
    }

    @Test
    void getKanjiByUuid_ShouldThrowException_WhenEntityNotFound() {
        UUID uuid = UUID.randomUUID();
        given(kanjiRepository.findByUuid(uuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> kanjiService.getKanjiByUuid(uuid))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getKanjiByStrokeNumber_ShouldReturnReferenceDtos() {
        int strokeCount = 5;
        UUID uuid = UUID.randomUUID();
        Kanji kanji = new Kanji();
        kanji.setUuid(uuid);
        kanji.setCharacter("田");

        List<List<List<Double>>> drawingData = List.of(List.of(List.of(1.0, 2.0)));
        kanji.setDrawingData(drawingData);

        given(kanjiRepository.findByDrawingDataCount(strokeCount)).willReturn(List.of(kanji));

        List<ReferenceKanjiDto> result = kanjiService.getKanjiByStrokeNumber(strokeCount);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().uuid()).isEqualTo(uuid);
        assertThat(result.getFirst().drawingData()).isEqualTo(drawingData);
    }
}
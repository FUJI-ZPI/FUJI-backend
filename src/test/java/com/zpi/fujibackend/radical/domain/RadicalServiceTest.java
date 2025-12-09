package com.zpi.fujibackend.radical.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.kanji.KanjiRadicalFacade;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.radical.dto.RadicalDetailDto;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.radical.dto.WanikaniRadicalJsonDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RadicalServiceTest {

    @Mock
    private RadicalRepository radicalRepository;

    @Mock
    private KanjiRadicalFacade kanjiRadicalFacade;

    @InjectMocks
    private RadicalService radicalService;

    @Test
    void getRadicalByLevel_ShouldReturnListOfRadicalDto() {
        int level = 1;
        UUID uuid = UUID.randomUUID();
        Radical radical = new Radical();
        radical.setUuid(uuid);
        radical.setCharacter("一");

        given(radicalRepository.findByLevel(level)).willReturn(List.of(radical));

        List<RadicalDto> result = radicalService.getRadicalByLevel(level);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().uuid()).isEqualTo(uuid);
        assertThat(result.getFirst().character()).isEqualTo("一");
    }

    @Test
    void getRadicalByLevel_ShouldReturnEmptyList_WhenNoRadicalsFound() {
        int level = 99;
        given(radicalRepository.findByLevel(level)).willReturn(Collections.emptyList());

        List<RadicalDto> result = radicalService.getRadicalByLevel(level);

        assertThat(result).isEmpty();
    }

    @Test
    void getRadicalByUuid_ShouldReturnDetailDtoWithRelatedKanjis_WhenJsonIsValid() {
        UUID uuid = UUID.randomUUID();
        String jsonDocument = "{}";

        Radical radical = new Radical();
        radical.setUuid(uuid);
        radical.setLevel(1);
        radical.setCharacter("大");
        radical.setSlug("big");
        radical.setDocument(jsonDocument);

        WanikaniRadicalJsonDto.Data data = new WanikaniRadicalJsonDto.Data(
                null, null, null, null, null, null, null, null, null, null, null,
                List.of(10, 20),
                null
        );
        WanikaniRadicalJsonDto wanikaniDto = new WanikaniRadicalJsonDto(1, null, data, null, null);

        List<KanjiDto> relatedKanjis = List.of(new KanjiDto(UUID.randomUUID(), "K"));

        given(radicalRepository.getByUuid(uuid)).willReturn(Optional.of(radical));
        given(kanjiRadicalFacade.getKanjisByIds(List.of(10L, 20L))).willReturn(relatedKanjis);

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(jsonDocument, WanikaniRadicalJsonDto.class))
                    .thenReturn(wanikaniDto);

            RadicalDetailDto result = radicalService.getRadicalByUuid(uuid);

            assertThat(result.character()).isEqualTo("大");
            assertThat(result.details()).isEqualTo(wanikaniDto);
            assertThat(result.kanjiDto()).hasSize(1);
            assertThat(result.kanjiDto()).isEqualTo(relatedKanjis);
        }
    }

    @Test
    void getRadicalByUuid_ShouldReturnDetailDtoWithoutRelatedKanjis_WhenIdsListIsEmpty() {
        UUID uuid = UUID.randomUUID();
        String jsonDocument = "{}";

        Radical radical = new Radical();
        radical.setUuid(uuid);
        radical.setDocument(jsonDocument);

        WanikaniRadicalJsonDto.Data data = new WanikaniRadicalJsonDto.Data(
                null, null, null, null, null, null, null, null, null, null, null,
                Collections.emptyList(),
                null
        );
        WanikaniRadicalJsonDto wanikaniDto = new WanikaniRadicalJsonDto(1, null, data, null, null);

        given(radicalRepository.getByUuid(uuid)).willReturn(Optional.of(radical));

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(jsonDocument, WanikaniRadicalJsonDto.class))
                    .thenReturn(wanikaniDto);

            RadicalDetailDto result = radicalService.getRadicalByUuid(uuid);

            assertThat(result.kanjiDto()).isEmpty();
            verify(kanjiRadicalFacade, never()).getKanjisByIds(anyList());
        }
    }

    @Test
    void getRadicalByUuid_ShouldHandleNullJsonDto() {
        UUID uuid = UUID.randomUUID();
        Radical radical = new Radical();
        radical.setUuid(uuid);
        radical.setDocument(null);

        given(radicalRepository.getByUuid(uuid)).willReturn(Optional.of(radical));

        try (MockedStatic<JsonConverter> jsonConverterMock = mockStatic(JsonConverter.class)) {
            jsonConverterMock.when(() -> JsonConverter.convertToDto(null, WanikaniRadicalJsonDto.class))
                    .thenReturn(null);

            RadicalDetailDto result = radicalService.getRadicalByUuid(uuid);

            assertThat(result.details()).isNull();
            assertThat(result.kanjiDto()).isEmpty();
            verify(kanjiRadicalFacade, never()).getKanjisByIds(any());
        }
    }

    @Test
    void getRadicalByUuid_ShouldThrowException_WhenRadicalNotFound() {
        UUID uuid = UUID.randomUUID();
        given(radicalRepository.getByUuid(uuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> radicalService.getRadicalByUuid(uuid))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(uuid.toString());
    }
}
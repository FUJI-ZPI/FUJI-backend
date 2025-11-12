package com.zpi.fujibackend.radical.domain;

import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.config.converter.JsonConverter;
import com.zpi.fujibackend.radical.RadicalFacade;
import com.zpi.fujibackend.radical.dto.RadicalDetailDto;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import com.zpi.fujibackend.radical.dto.WanikaniRadicalJsonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class RadicalService implements RadicalFacade {

    private final RadicalRepository radicalRepository;


    @Override
    public List<RadicalDto> getRadicalByLevel(int level) {
        return radicalRepository.findByLevel(level).stream()
                .map(
                        radical -> new RadicalDto(radical.getUuid(), radical.getCharacter())
                )
                .toList();
    }


    @Override
    public RadicalDetailDto getRadicalByUuid(UUID uuid) {
        return radicalRepository.getByUuid(uuid)
                .map(
                        radical -> new RadicalDetailDto(
                                radical.getLevel(),
                                radical.getCharacter(),
                                radical.getCharacterUnicode(),
                                radical.getSlug(),
                                JsonConverter.convertToDto(radical.getDocument(), WanikaniRadicalJsonDto.class)
                        ))
                .orElseThrow(() -> new NotFoundException("No Radical for UUID: " + uuid));

    }
}

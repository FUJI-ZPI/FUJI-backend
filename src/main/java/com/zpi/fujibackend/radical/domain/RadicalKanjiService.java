package com.zpi.fujibackend.radical.domain;

import com.zpi.fujibackend.radical.RadicalKanjiFacade;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class RadicalKanjiService implements RadicalKanjiFacade {

    private final RadicalRepository radicalRepository;

    @Override
    public List<RadicalDto> getRadicalsByIds(final List<Long> ids) {
        return radicalRepository.findAllById(ids).stream()
                .map(radical -> new RadicalDto(radical.getUuid(), radical.getCharacter()))
                .toList();
    }
}

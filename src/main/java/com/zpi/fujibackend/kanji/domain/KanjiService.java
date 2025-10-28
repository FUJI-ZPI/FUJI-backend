package com.zpi.fujibackend.kanji.domain;

import com.zpi.fujibackend.kanji.KanjiFacade;
import com.zpi.fujibackend.kanji.dto.KanjiCharacterDto;
import com.zpi.fujibackend.kanji.dto.KanjiDto;
import com.zpi.fujibackend.kanji.mapper.KanjiDtoMapper;
import com.zpi.fujibackend.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class KanjiService implements KanjiFacade {

    private final KanjiRepository kanjiRepository;
    private final KanjiDtoMapper kanjiDtoMapper;
    private final UserFacade userFacade;

    @Override
    public List<KanjiCharacterDto> getKanjisByLevel(int level) {
        return kanjiRepository.findByLevel(level)
                .stream()
                .map(kanji -> new KanjiCharacterDto(kanji.getUuid(), kanji.getCharacter()))
                .toList();
    }

    @Override
    public List<KanjiDto> getKanjisNotInCards() {
        return kanjiDtoMapper.toDtoList(
                kanjiRepository.findAllNotInCardsForUser(userFacade.getCurrentUserId(), userFacade.getCurrentUserLevel())
        );
    }

    @Override
    public Kanji getKanjiByUuid(UUID uuid) {
        return kanjiRepository.findByUuid(uuid);
    }
}

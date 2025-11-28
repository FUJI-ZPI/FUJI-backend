package com.zpi.fujibackend.progress.dto;

import com.zpi.fujibackend.kanji.dto.KanjiDto;

import java.util.List;

public record KanjiRemainingDto(Integer amount,
                                List<KanjiDto> kanji) {

}
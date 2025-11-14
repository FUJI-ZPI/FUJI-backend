package com.zpi.fujibackend.radical;

import com.zpi.fujibackend.radical.dto.RadicalDetailDto;
import com.zpi.fujibackend.radical.dto.RadicalDto;

import java.util.List;
import java.util.UUID;

public interface RadicalFacade {

    List<RadicalDto> getRadicalByLevel(int level);

    RadicalDetailDto getRadicalByUuid(UUID uuid);
}

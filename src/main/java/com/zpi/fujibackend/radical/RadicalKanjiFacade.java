package com.zpi.fujibackend.radical;

import com.zpi.fujibackend.radical.dto.RadicalDto;

import java.util.List;

public interface RadicalKanjiFacade {

    List<RadicalDto> getRadicalsByIds(List<Long> ids);
}

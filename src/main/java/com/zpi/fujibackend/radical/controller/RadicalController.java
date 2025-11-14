package com.zpi.fujibackend.radical.controller;

import com.zpi.fujibackend.radical.RadicalFacade;
import com.zpi.fujibackend.radical.dto.RadicalDetailDto;
import com.zpi.fujibackend.radical.dto.RadicalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/radical")
class RadicalController {

    private final RadicalFacade radicalFacade;

    private static final class Routes {
        private static final String LEVEL = "/level/{level}";
        private static final String DETAILS = "/{uuid}";

    }

    @GetMapping(Routes.LEVEL)
    List<RadicalDto> getRadicalByLevel(@PathVariable int level) {
        return radicalFacade.getRadicalByLevel(level);

    }

    @GetMapping(Routes.DETAILS)
    RadicalDetailDto getRadicalByUuid(@PathVariable UUID uuid) {
        return radicalFacade.getRadicalByUuid(uuid);
    }


}

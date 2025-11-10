package com.zpi.fujibackend.recognizer.controller;

import com.zpi.fujibackend.recognizer.RecognizerFacade;
import com.zpi.fujibackend.recognizer.dto.RecognizeForm;
import com.zpi.fujibackend.recognizer.dto.RecognizedKanjiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recognizer")
@RequiredArgsConstructor
class RecognizerController {

    private final RecognizerFacade recognizerFacade;

    @PostMapping("/recognize")
    List<RecognizedKanjiDto> recognize(@RequestBody RecognizeForm form) {
        return recognizerFacade.recognize(form);
    }
}

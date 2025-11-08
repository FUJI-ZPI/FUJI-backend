package com.zpi.fujibackend.recognizer;

import com.zpi.fujibackend.recognizer.dto.RecognizeForm;
import com.zpi.fujibackend.recognizer.dto.RecognizedKanjiDto;

import java.util.List;

public interface RecognizerFacade {

    List<RecognizedKanjiDto> recognize(RecognizeForm form);
}

package com.zpi.fujibackend.chatbot.dto;


public record LearnedKanjiInfoDto(
        String character,
        String meaning,
        String reading
) {
    public String toPromptString() {
        return character + " (" + meaning + ", " + reading + ")";
    }
}

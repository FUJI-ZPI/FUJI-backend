package com.zpi.fujibackend.recognizer.domain;

import java.util.UUID;

record RecognitionCandidate(int referenceIndex,
                            UUID uuid,
                            String kanjiChar,
                            double distance) implements Comparable<RecognitionCandidate> {
    @Override
    public int compareTo(RecognitionCandidate other) {
        return Double.compare(this.distance, other.distance);
    }
}
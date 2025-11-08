package com.zpi.fujibackend.recognizer.domain;

import java.util.List;

record SortedStrokePair(List<List<Double>> longerStroke,
                        List<List<Double>> shorterStroke,
                        int longerStrokePointCount,
                        int shorterStrokePointCount) {

}
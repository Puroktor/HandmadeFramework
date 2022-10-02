package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDto {
    private Integer userId;
    private String nickname;
    private Double score;
    private Timestamp dateTime;
    private TestDto test;
    private Map<Integer, Boolean> answerToSubmittedValueMap;
}

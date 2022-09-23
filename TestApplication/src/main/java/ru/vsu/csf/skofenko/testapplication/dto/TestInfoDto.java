package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.Data;

@Data
public class TestInfoDto {
    private Integer id;
    private String programmingLang;
    private String name;
    private Integer passingScore;
    private Integer questionsCount;
    private Double userScore;
}

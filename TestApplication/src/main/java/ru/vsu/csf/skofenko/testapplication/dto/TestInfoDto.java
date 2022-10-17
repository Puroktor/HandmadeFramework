package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestInfoDto {
    private Integer id;
    private String programmingLang;
    private String name;
    private Integer passingScore;
    private Integer questionsCount;
    private Double userScore;
}

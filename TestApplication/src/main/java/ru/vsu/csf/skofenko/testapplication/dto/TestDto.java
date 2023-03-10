package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.csf.skofenko.testapplication.entity.TestType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

    private Integer id;
    private String programmingLang;
    private String name;
    private Integer questionsCount;
    private Integer passingScore;
    private TestType testType;
    private List<QuestionDto> questions;
}

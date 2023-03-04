package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.skofenko.testapplication.entity.TestType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DisplayName("Custom test dto")
public class TestDto {

    private Integer id;

    @DisplayName("Custom programming language")
    private String programmingLang;
    private String name;
    private Integer questionsCount;
    private Integer passingScore;
    private TestType testType;
    private List<QuestionDto> questions;
}

package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Integer id;
    private String text;
    private Integer maxScore;
    private Integer questionTemplateIndex;
    private List<AnswerDto> answers;
}

package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Integer id;
    private Test test;
    private String text;
    private Integer maxScore;
    private Integer questionTemplateIndex;
    private List<Answer> answers;
}

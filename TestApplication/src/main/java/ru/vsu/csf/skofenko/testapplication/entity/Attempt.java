package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    private Integer id;
    private User user;
    private Test test;
    private Double score;
    private LocalDateTime dateTime;
    private Map<Answer, Boolean> submittedAnswers;
}

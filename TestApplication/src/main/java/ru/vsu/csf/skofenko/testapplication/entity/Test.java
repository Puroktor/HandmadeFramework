package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    private Integer id;
    private String programmingLang;
    private String name;
    private Integer questionsCount;
    private Integer passingScore;
    private TestType testType;
    private List<Attempt> attempts;
    private List<Question> questions;
}

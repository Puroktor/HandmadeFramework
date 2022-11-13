package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.csf.framework.persistence.Entity;
import ru.vsu.csf.framework.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    private Integer id;
    private Integer testId;
    private String text;
    private Integer maxScore;
    private Integer questionTemplateIndex;
}

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
public class Test {
    @Id
    private Integer id;
    private String programmingLang;
    private String name;
    private Integer questionsCount;
    private Integer passingScore;
    private TestType testType;
}

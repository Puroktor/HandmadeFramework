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
public class SubmittedAnswer {
    @Id
    private Integer answerId;
    @Id
    private Integer attemptId;
    private Boolean submittedValue;
}

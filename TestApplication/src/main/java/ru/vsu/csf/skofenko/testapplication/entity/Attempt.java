package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.csf.framework.persistence.Entity;
import ru.vsu.csf.framework.persistence.Id;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    @Id
    private Integer id;
    private Integer userId;
    private Integer testId;
    private Double score;
    private Timestamp dateTime;
}

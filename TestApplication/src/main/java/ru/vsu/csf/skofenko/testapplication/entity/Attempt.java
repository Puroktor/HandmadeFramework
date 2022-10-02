package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    private Integer id;
    private Integer userId;
    private Integer testId;
    private Double score;
    private Timestamp dateTime;
}

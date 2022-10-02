package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    private Integer id;
    private Integer testId;
    private String testName;
    private Timestamp dateTime;
    private Double score;
    private Boolean hasPassed;
}

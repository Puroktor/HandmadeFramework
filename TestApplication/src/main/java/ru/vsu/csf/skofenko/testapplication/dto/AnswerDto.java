package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private Integer id;
    private String text;
    private Boolean isRight;
}

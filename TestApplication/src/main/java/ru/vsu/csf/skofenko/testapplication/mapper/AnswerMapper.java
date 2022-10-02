package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.entity.Answer;

public class AnswerMapper {
    public AnswerDto toDto(Answer answer) {
        return new AnswerDto(answer.getId(), answer.getText(), answer.getIsRight());
    }

    public Answer toEntity(AnswerDto dto, Integer questionId) {
        return new Answer(dto.getId(), dto.getText(), dto.getIsRight(), questionId);
    }
}

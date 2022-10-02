package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.QuestionDto;
import ru.vsu.csf.skofenko.testapplication.entity.Question;

import java.util.List;

public class QuestionMapper {
    public QuestionDto toDto(Question question, List<AnswerDto> answersDtoList) {
        return new QuestionDto(question.getId(), question.getText(), question.getMaxScore(),
                question.getQuestionTemplateIndex(), answersDtoList);
    }

    public Question toEntity(QuestionDto dto, Integer testId) {
        return new Question(dto.getId(), testId, dto.getText(), dto.getMaxScore(), dto.getQuestionTemplateIndex());
    }
}

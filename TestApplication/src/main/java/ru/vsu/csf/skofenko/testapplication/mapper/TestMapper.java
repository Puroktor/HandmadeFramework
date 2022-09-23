package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.annotations.di.Component;
import ru.vsu.csf.annotations.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.QuestionDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.entity.Answer;
import ru.vsu.csf.skofenko.testapplication.entity.Question;
import ru.vsu.csf.skofenko.testapplication.entity.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestMapper {

    public TestDto toDto(Test test) {
        TestDto testDto = new TestDto(test.getId(), test.getProgrammingLang(), test.getName(), test.getQuestionsCount(),
                test.getPassingScore(), test.getTestType(), null);
        List<QuestionDto> questionsDtoList = new ArrayList<>();
        for (Question question : test.getQuestions()) {
            List<AnswerDto> answersDtoList = question.getAnswers().stream()
                    .map((ans) -> new AnswerDto(ans.getId(), ans.getText(), ans.getIsRight()))
                    .collect(Collectors.toList());
            QuestionDto questionDto = new QuestionDto(question.getId(), question.getText(), question.getMaxScore(),
                    question.getQuestionTemplateIndex(), answersDtoList);
            questionsDtoList.add(questionDto);
        }
        testDto.setQuestions(questionsDtoList);
        return testDto;
    }

    public Test toEntity(TestDto testDto) {
        Test test = new Test(testDto.getId(), testDto.getProgrammingLang(), testDto.getName(), testDto.getQuestionsCount(),
                testDto.getPassingScore(), testDto.getTestType(), new ArrayList<>(), new ArrayList<>());
        for (QuestionDto questionDto : testDto.getQuestions()) {
            Question question = new Question(questionDto.getId(), test, questionDto.getText(),
                    questionDto.getMaxScore(), questionDto.getQuestionTemplateIndex(), null);
            List<Answer> answers = questionDto.getAnswers().stream()
                    .map((ans) -> new Answer(ans.getId(), ans.getText(), ans.getIsRight(), question))
                    .collect(Collectors.toList());
            question.setAnswers(answers);
            test.getQuestions().add(question);
        }
        return test;
    }
}

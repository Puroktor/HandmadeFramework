package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.skofenko.testapplication.dto.QuestionDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.entity.Test;

import java.util.List;

public class TestMapper {

    public TestDto toDto(Test test, List<QuestionDto> questionsDtoList) {
        TestDto testDto = new TestDto(test.getId(), false, test.getProgrammingLang(), test.getName(), test.getQuestionsCount(),
                test.getPassingScore(), test.getTestType(), questionsDtoList);
        testDto.setQuestions(questionsDtoList);
        return testDto;
    }

    public Test toEntity(TestDto testDto) {
        return new Test(testDto.getId(), testDto.getProgrammingLang(), testDto.getName(), testDto.getQuestionsCount(),
                testDto.getPassingScore(), testDto.getTestType());
    }
}

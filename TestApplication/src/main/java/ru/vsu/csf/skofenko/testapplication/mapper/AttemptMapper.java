package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.skofenko.testapplication.dto.AttemptResultDto;
import ru.vsu.csf.skofenko.testapplication.entity.Attempt;
import ru.vsu.csf.skofenko.testapplication.entity.Test;

public class AttemptMapper {

    public AttemptResultDto toResultDto(Attempt attempt) {
        Test test = attempt.getTest();
        return new AttemptResultDto(attempt.getId(), test.getId(), test.getName(), attempt.getDateTime(),
                attempt.getScore(), attempt.getScore() >= test.getPassingScore());
    }
}

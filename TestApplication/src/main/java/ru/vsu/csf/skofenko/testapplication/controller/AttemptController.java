package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.ResponseStatus;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptResultDto;
import ru.vsu.csf.skofenko.testapplication.service.AttemptService;

import java.util.List;

@Controller("api")
public class AttemptController {
    @Inject
    private AttemptService attemptService;

    @PostMapping("attempt")
    @ResponseStatus(HttpStatus.CREATED)
    public AttemptResultDto submitAttempt(@Param("userId") int userId, @RequestBody List<AnswerDto> answers) {
        return attemptService.submitAttempt(answers, userId);
    }

    @GetMapping("attempt")
    public AttemptDto getAttempt(@Param("attemptId") int attemptId) {
        return attemptService.getAttempt(attemptId);
    }

    @GetMapping("attempt-list")
    public List<AttemptResultDto> getAttemptsResults(@Param("userId") int userId) {
        return attemptService.getAttemptsResults(userId);
    }
}

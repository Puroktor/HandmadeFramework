package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.annotations.di.Controller;
import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.http.*;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptResultDto;
import ru.vsu.csf.skofenko.testapplication.service.AttemptService;

import java.util.List;

@Controller("api/")
public class AttemptController {
    @Inject
    private AttemptService attemptService;

    @PostMapping("submit-attempt")
    @ResponseType(HttpStatus.CREATED)
    public AttemptResultDto submitAttempt(@Param("nickname") String nickname, @RequestBody List<AnswerDto> answers) {
        return attemptService.submitAttempt(answers, nickname);
    }

    @GetMapping("get-attempt")
    public AttemptDto getAttempt(@Param("attemptId") int attemptId) {
        return attemptService.getAttempt(attemptId);
    }

    @GetMapping("get-attempt-list")
    public List<AttemptResultDto> getAttemptsResults(@Param("userId") int userId) {
        return attemptService.getAttemptsResults(userId);
    }
}

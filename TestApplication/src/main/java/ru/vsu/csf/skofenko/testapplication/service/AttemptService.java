package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptResultDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.entity.*;
import ru.vsu.csf.skofenko.testapplication.mapper.AttemptMapper;
import ru.vsu.csf.skofenko.testapplication.mapper.TestMapper;
import ru.vsu.csf.skofenko.testapplication.repository.AnswerRepository;
import ru.vsu.csf.skofenko.testapplication.repository.AttemptRepository;
import ru.vsu.csf.skofenko.testapplication.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AttemptService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private AnswerRepository answerRepository;
    @Inject
    private AttemptRepository attemptRepository;
    @Inject
    private TestMapper testMapper;
    @Inject
    private AttemptMapper attemptMapper;

    public AttemptResultDto submitAttempt(List<AnswerDto> answers, String nickname) {
        class Score {
            private int correct;
            private int all;
        }
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Invalid user nickname"));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        for (AnswerDto submittedAnswer : answers) {
            Answer dbAnswer = answerRepository.findById(submittedAnswer.getId())
                    .orElseThrow(() -> new NoSuchElementException("Invalid answer Id"));
            Question question = dbAnswer.getQuestion();
            Score score = questionIdToScoreMap.getOrDefault(question, new Score());
            if (submittedAnswer.getIsRight()) {
                score.correct += dbAnswer.getIsRight() ? 1 : -1;
            }
            if (dbAnswer.getIsRight()) {
                score.all++;
            }
            questionIdToScoreMap.put(question, score);
            submittedAnswers.put(dbAnswer, submittedAnswer.getIsRight());
        }
        double score = 0, maxScore = 0;
        for (Map.Entry<Question, Score> entry : questionIdToScoreMap.entrySet()) {
            score += entry.getKey().getMaxScore() * Math.max((double) entry.getValue().correct / entry.getValue().all, 0);
            maxScore += entry.getKey().getMaxScore();
        }
        double scorePercentage = score / maxScore * 100;
        Test test = questionIdToScoreMap.keySet().iterator().next().getTest();
        Attempt attempt = attemptRepository.save(new Attempt(null, user, test, scorePercentage, LocalDateTime.now(),
                submittedAnswers));
        return attemptMapper.toResultDto(attempt);
    }

    public List<AttemptResultDto> getAttemptsResults(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user nickname"));
        List<Attempt> attempts = attemptRepository.findAllByUserOrderByDateTimeDesc(user);
        return attempts.stream()
                .map(attemptMapper::toResultDto)
                .collect(Collectors.toList());
    }

    public AttemptDto getAttempt(int attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("No such attempt"));
        TestDto testDto = testMapper.toDto(attempt.getTest());
        Map<Integer, Boolean> submittedAnswers = attempt.getSubmittedAnswers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
        testDto.setQuestions(
                testDto.getQuestions().stream()
                        .filter(questionDto -> submittedAnswers.containsKey(questionDto.getAnswers().get(0).getId()))
                        .collect(Collectors.toList()));
        return new AttemptDto(attempt.getUser().getId(), attempt.getUser().getNickname(),
                attempt.getScore(), attempt.getDateTime(), testDto, submittedAnswers);
    }
}

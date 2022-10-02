package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptDto;
import ru.vsu.csf.skofenko.testapplication.dto.AttemptResultDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.entity.*;
import ru.vsu.csf.skofenko.testapplication.mapper.AttemptMapper;
import ru.vsu.csf.skofenko.testapplication.repository.*;

import java.sql.Timestamp;
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
    private QuestionRepository questionRepository;
    @Inject
    private TestRepository testRepository;
    @Inject
    private SubmittedAnswerRepository submittedAnswerRepository;
    @Inject
    private AttemptRepository attemptRepository;
    @Inject
    private AttemptMapper attemptMapper;
    @Inject
    private TestService testService;

    public AttemptResultDto submitAttempt(List<AnswerDto> answers, int userId) {
        class Score {
            private int correct;
            private int all;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        for (AnswerDto submittedAnswer : answers) {
            Answer dbAnswer = answerRepository.findById(submittedAnswer.getId())
                    .orElseThrow(() -> new NoSuchElementException("Invalid answer Id"));
            Question question = questionRepository.findById(dbAnswer.getQuestionId())
                    .orElseThrow(() -> new NoSuchElementException("Invalid question Id"));
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
        int testId = questionIdToScoreMap.keySet().iterator().next().getTestId();
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NoSuchElementException("Invalid test id"));
        Attempt attempt = attemptRepository.save(new Attempt(null, userId, testId, scorePercentage,
                new Timestamp(System.currentTimeMillis())));
        for (Map.Entry<Answer, Boolean> entry : submittedAnswers.entrySet()) {
            submittedAnswerRepository.save(new SubmittedAnswer(entry.getKey().getId(), attempt.getId(), entry.getValue()));
        }
        return attemptMapper.toResultDto(attempt, test);
    }

    public List<AttemptResultDto> getAttemptsResults(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        List<Attempt> attempts = attemptRepository.findAllByUserIdOrderByDateTimeDesc(userId);
        return attempts.stream()
                .map((attempt) -> {
                            Test test = testRepository.findById(attempt.getTestId())
                                    .orElseThrow(() -> new NoSuchElementException("Invalid test id"));
                            return attemptMapper.toResultDto(attempt, test);
                        }
                )
                .collect(Collectors.toList());
    }

    public AttemptDto getAttempt(int attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("No such attempt"));
        User user = userRepository.findById(attempt.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        TestDto testDto = testService.getTest(attempt.getTestId());
        Map<Integer, Boolean> submittedAnswers = submittedAnswerRepository.getAllByAttemptId(attemptId)
                .stream()
                .collect(Collectors.toMap(SubmittedAnswer::getAnswerId, SubmittedAnswer::isSubmittedValue));
        testDto.setQuestions(
                testDto.getQuestions().stream()
                        .filter(questionDto -> submittedAnswers.containsKey(questionDto.getAnswers().get(0).getId()))
                        .collect(Collectors.toList()));
        return new AttemptDto(user.getId(), user.getNickname(), attempt.getScore(), attempt.getDateTime(),
                testDto, submittedAnswers);
    }
}

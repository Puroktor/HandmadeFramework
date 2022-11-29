package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.framework.persistence.EntityManager;
import ru.vsu.csf.skofenko.testapplication.dto.*;
import ru.vsu.csf.skofenko.testapplication.entity.*;
import ru.vsu.csf.skofenko.testapplication.mapper.AttemptMapper;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttemptService {
    @Inject
    private EntityManager entityManager;
    @Inject
    private AttemptMapper attemptMapper;
    @Inject
    private TestService testService;

    public AttemptResultDto submitAttempt(AnswerDto[] answers, int userId) {
        class Score {
            private int correct;
            private int all;
        }
        entityManager.find(User.class, userId).orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        for (AnswerDto submittedAnswer : answers) {
            Answer dbAnswer = entityManager.find(Answer.class, submittedAnswer.getId())
                    .orElseThrow(() -> new NoSuchElementException("Invalid answer Id"));
            Question question = entityManager.find(Question.class, dbAnswer.getQuestionId())
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
        Test test = entityManager.find(Test.class, testId)
                .orElseThrow(() -> new NoSuchElementException("Invalid test id"));
        Attempt attempt = new Attempt(null, userId, testId, scorePercentage, new Timestamp(System.currentTimeMillis()));
        entityManager.save(attempt);
        for (Map.Entry<Answer, Boolean> entry : submittedAnswers.entrySet()) {
            entityManager.save(new SubmittedAnswer(entry.getKey().getId(), attempt.getId(), entry.getValue()));
        }
        return attemptMapper.toResultDto(attempt, test);
    }

    public List<AttemptResultDto> getAttemptsResults(int userId) {
        entityManager.find(User.class, userId).orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        List<Attempt> attempts =
                entityManager.executeNativeQuery("SELECT * FROM attempt WHERE user_id = ? ORDER BY date_time DESC",
                        Attempt.class,
                        List.of(userId));
        return attempts.stream()
                .map((attempt) -> {
                            Test test = entityManager.find(Test.class, attempt.getTestId())
                                    .orElseThrow(() -> new NoSuchElementException("Invalid test id"));
                            return attemptMapper.toResultDto(attempt, test);
                        }
                )
                .collect(Collectors.toList());
    }

    public AttemptDto getAttempt(int attemptId) {
        Attempt attempt = entityManager.find(Attempt.class, attemptId)
                .orElseThrow(() -> new NoSuchElementException("No such attempt"));
        User user = entityManager.find(User.class, attempt.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Invalid user id"));
        TestDto testDto = testService.getTest(attempt.getTestId());
        Map<Integer, Boolean> submittedAnswers = entityManager.findAllByProperties(SubmittedAnswer.class, Map.of("attempt_id", attemptId))
                .stream()
                .collect(Collectors.toMap(SubmittedAnswer::getAnswerId, SubmittedAnswer::getSubmittedValue));
        testDto.setQuestions(
                testDto.getQuestions().stream()
                        .filter(questionDto -> submittedAnswers.containsKey(questionDto.getAnswers().get(0).getId()))
                        .collect(Collectors.toList()));
        return new AttemptDto(user.getId(), user.getNickname(), attempt.getScore(), attempt.getDateTime(),
                testDto, submittedAnswers);
    }

    public LeaderboardDto getLeaderboard() {
        List<User> userList = entityManager.findAllByProperties(User.class, Map.of("role", Role.STUDENT));
        if (userList.isEmpty()) {
            return new LeaderboardDto(null, null);
        }
        Iterable<Test> testList = entityManager.findAll(Test.class);
        List<LeaderboardDto.UserRecord> userRecords = new ArrayList<>();
        for (User user : userList) {
            Map<Integer, Double> testIdToScoreMap = new HashMap<>();
            Double total = 0d;
            for (Test test : testList) {
                List<Attempt> attempts = entityManager.executeNativeQuery(
                        "SELECT * FROM attempt WHERE user_id = ? AND test_id = ? ORDER BY date_time LIMIT 1",
                        Attempt.class,
                        List.of(user.getId(), test.getId()));
                if (!attempts.isEmpty()) {
                    Attempt attempt = attempts.get(0);
                    total += attempt.getScore();
                    testIdToScoreMap.put(attempt.getTestId(), attempt.getScore());
                }
            }
            userRecords.add(new LeaderboardDto.UserRecord(user.getId(), user.getNickname(), total, testIdToScoreMap));
        }
        List<LeaderboardDto.TestRecord> testRecordList = new ArrayList<>();
        for (Test test : testList) {
            testRecordList.add(new LeaderboardDto.TestRecord(test.getId(), test.getName(), test.getPassingScore()));
        }
        return new LeaderboardDto(testRecordList, userRecords);
    }
}

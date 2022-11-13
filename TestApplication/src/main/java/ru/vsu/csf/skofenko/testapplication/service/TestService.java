package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.framework.persistence.EntityManager;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.QuestionDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestInfoDto;
import ru.vsu.csf.skofenko.testapplication.entity.*;
import ru.vsu.csf.skofenko.testapplication.mapper.AnswerMapper;
import ru.vsu.csf.skofenko.testapplication.mapper.QuestionMapper;
import ru.vsu.csf.skofenko.testapplication.mapper.TestMapper;

import java.util.*;

@Service
public class TestService {
    @Inject
    private EntityManager entityManager;
    @Inject
    private TestMapper testMapper;
    @Inject
    private QuestionMapper questionMapper;
    @Inject
    private AnswerMapper answerMapper;

    public TestDto createTest(TestDto testDto) {
        validateTestDto(testDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
        if (testDto.getId() != null) {
            throw new IllegalArgumentException("Test must not have id");
        }
        return saveTest(testDto);
    }

    private TestDto saveTest(TestDto testDto) {
        Test test = testMapper.toEntity(testDto);
        entityManager.save(test);
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for (QuestionDto questionDto : testDto.getQuestions()) {
            Question question = questionMapper.toEntity(questionDto, test.getId());
            entityManager.save(question);
            List<AnswerDto> answerDtoList = new ArrayList<>();
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = answerMapper.toEntity(answerDto, question.getId());
                entityManager.save(answer);
                answerDtoList.add(answerMapper.toDto(answer));
            }
            questionDtoList.add(questionMapper.toDto(question, answerDtoList));
        }
        return testMapper.toDto(test, questionDtoList);
    }

    public TestDto getTest(int id) {
        Test test = entityManager.find(Test.class, id).orElseThrow(() -> new NoSuchElementException("Invalid test Id"));
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for (Question question : entityManager.findAllByProperties(Question.class, Map.of("testId", test.getId()))) {
            List<AnswerDto> answersDtoList = entityManager.findAllByProperties(Answer.class, Map.of("questionId", question.getId()))
                    .stream()
                    .map((ans) -> answerMapper.toDto(ans))
                    .toList();
            QuestionDto questionDto = questionMapper.toDto(question, answersDtoList);
            questionDtoList.add(questionDto);
        }
        return testMapper.toDto(test, questionDtoList);
    }

    public TestDto getShuffledTest(int id) {
        TestDto testDto = getTest(id);
        if (testDto.getTestType() == TestType.WITH_BANK) {
            Collections.shuffle(testDto.getQuestions());
            List<QuestionDto> questions = testDto.getQuestions().subList(0, testDto.getQuestionsCount());
            for (QuestionDto questionDto : questions) {
                questionDto.getAnswers().forEach((answerDto -> answerDto.setIsRight(false)));
            }
            testDto.setQuestions(questions);
        } else if (testDto.getTestType() == TestType.WITH_QUESTION_OPTIONS) {
            List<List<QuestionDto>> questionTemplates = new ArrayList<>();
            for (int i = 0; i < testDto.getQuestionsCount(); i++) {
                questionTemplates.add(new ArrayList<>());
            }
            for (QuestionDto questionDto : testDto.getQuestions()) {
                int index = questionDto.getQuestionTemplateIndex();
                List<QuestionDto> questionOptions = questionTemplates.get(index);
                questionOptions.add(questionDto);
            }
            Random rand = new Random();
            List<QuestionDto> questions = new ArrayList<>();
            for (List<QuestionDto> questionOptions : questionTemplates) {
                QuestionDto questionDto = questionOptions.get(rand.nextInt(questionOptions.size()));
                questionDto.getAnswers().forEach((answerDto -> answerDto.setIsRight(false)));
                questions.add(questionDto);
            }
            testDto.setQuestions(questions);
        }
        return testDto;
    }

    public List<TestInfoDto> getTestList(int userId) {
        entityManager.find(User.class, userId).orElseThrow(() -> new NoSuchElementException("User doesn't exist"));
        List<Test> tests = entityManager.findAll(Test.class);
        List<TestInfoDto> list = new ArrayList<>();
        for (Test test : tests) {
            List<Attempt> attempts = entityManager.executeNativeQuery(
                    "SELECT * FROM attempt WHERE user_id = ? AND test_id = ? ORDER BY date_time LIMIT 1",
                    Attempt.class,
                    List.of(userId, test.getId()));
            Double userScore = attempts.isEmpty() ? 0 : attempts.get(0).getScore();
            TestInfoDto testInfoDto = new TestInfoDto(test.getId(), test.getProgrammingLang(), test.getName(),
                    test.getPassingScore(), test.getPassingScore(), userScore);
            list.add(testInfoDto);
        }
        return list;
    }

    public void updateTest(int id, TestDto newTestDto) {
        validateTestDto(newTestDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
        newTestDto.setId(id);

        entityManager.remove(Test.class, id);
        saveTest(newTestDto);
    }

    public void deleteTest(int id) {
        entityManager.find(Test.class, id).orElseThrow(() -> new NoSuchElementException("Invalid test Id"));
        entityManager.remove(Test.class, id);
    }

    private Optional<String> validateTestDto(TestDto testDto) {
        Set<String> violations = new HashSet<>();
        findViolationsInCommon(testDto, violations);
        if (testDto.getTestType() == TestType.WITH_BANK) {
            findViolationsInBank(testDto, violations);
        } else if (testDto.getTestType() == TestType.WITH_QUESTION_OPTIONS) {
            findViolationsInOptions(testDto, violations);
        }
        StringBuilder violationsBuilder = new StringBuilder();
        violations.forEach(violation -> violationsBuilder.append(violation).append('\n'));
        String violationsMessage = violationsBuilder.toString();
        return Optional.ofNullable(violationsMessage.equals("") ? null : violationsMessage);
    }

    private void findViolationsInCommon(TestDto testDto, Set<String> violations) {
        for (QuestionDto questionDto : testDto.getQuestions()) {
            boolean hasRightAnswer = questionDto.getAnswers().stream()
                    .map(AnswerDto::getIsRight)
                    .reduce(false, (a, b) -> a || b);
            if (!hasRightAnswer) {
                violations.add("Question must have at least 1 right answer");
                break;
            }
        }
    }

    private void findViolationsInBank(TestDto testDto, Set<String> violations) {
        if (testDto.getQuestionsCount() > testDto.getQuestions().size()) {
            violations.add("Question count for student must be <= question bank size");
        }
        for (QuestionDto questionDto : testDto.getQuestions()) {
            if (questionDto.getQuestionTemplateIndex() != null) {
                violations.add("Question must not have template index");
                break;
            }
        }
    }

    private void findViolationsInOptions(TestDto testDto, Set<String> violations) {
        boolean[] questionTemplatesPresence = new boolean[testDto.getQuestionsCount()];
        for (QuestionDto questionDto : testDto.getQuestions()) {
            Integer index = questionDto.getQuestionTemplateIndex();
            if (index == null) {
                violations.add("Enter question template index");
            } else if (questionDto.getQuestionTemplateIndex() >= testDto.getQuestionsCount()) {
                violations.add("Question template index must be < question count for students");
            } else {
                questionTemplatesPresence[index] = true;
            }
        }
        for (boolean isPresent : questionTemplatesPresence) {
            if (!isPresent) {
                violations.add("All question templates must have at least 1 question option");
                break;
            }
        }
    }
}

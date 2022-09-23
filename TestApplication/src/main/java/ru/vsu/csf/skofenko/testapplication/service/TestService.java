package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.AnswerDto;
import ru.vsu.csf.skofenko.testapplication.dto.QuestionDto;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.entity.Test;
import ru.vsu.csf.skofenko.testapplication.entity.TestType;
import ru.vsu.csf.skofenko.testapplication.mapper.TestMapper;
import ru.vsu.csf.skofenko.testapplication.repository.TestRepository;

import java.util.*;

@Service
public class TestService {
    @Inject
    private TestRepository testRepository;
    @Inject
    private TestMapper testMapper;

    public TestDto createTest(TestDto testDto) {
        validateTestDto(testDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
        if (testDto.getId() != null) {
            throw new IllegalArgumentException("Test must not have id");
        }
        Test test = testMapper.toEntity(testDto);
        test = testRepository.save(test);
        return testMapper.toDto(test);
    }

    public TestDto getTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id"));
        return testMapper.toDto(test);
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

    public void updateTest(int id, TestDto newTestDto) {
        validateTestDto(newTestDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
        Test newTest = testMapper.toEntity(newTestDto);
        newTest.setId(id);
        testRepository.delete(id);
        testRepository.save(newTest);
    }

    public void deleteTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id"));
        testRepository.delete(test);
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

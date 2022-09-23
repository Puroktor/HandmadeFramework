package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.annotations.di.Controller;
import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.http.*;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.service.TestService;

@Controller("api/")
public class TestController {
    @Inject
    private TestService testService;

    @PostMapping("create-test")
    @ResponseType(HttpStatus.CREATED)
    public TestDto createTest(@RequestBody TestDto testDto) {
        return testService.createTest(testDto);
    }

    @GetMapping("get-test")
    public TestDto getTest(@Param("id") int id) {
        return testService.getTest(id);
    }

    @GetMapping("shuffled-test")
    public TestDto getShuffledTest(@Param("id") int id) {
        return testService.getShuffledTest(id);
    }

    @PostMapping("update-test")
    public void updateTest(@Param("id") int id, @RequestBody TestDto testDto) {
        testService.updateTest(id, testDto);
    }

    @PostMapping("delete-test")
    public void deleteTest(@Param("id") int id) {
        testService.deleteTest(id);
    }
}

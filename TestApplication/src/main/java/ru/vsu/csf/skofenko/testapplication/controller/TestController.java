package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.ResponseStatus;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.testapplication.dto.TestDto;
import ru.vsu.csf.skofenko.testapplication.service.TestService;

@Controller("api")
public class TestController {
    @Inject
    private TestService testService;

    @PostMapping("test")
    @ResponseStatus(HttpStatus.CREATED)
    public TestDto createTest(@RequestBody TestDto testDto) {
        return testService.createTest(testDto);
    }

    @GetMapping("test")
    public TestDto getTest(@Param("id") int id) {
        return testService.getTest(id);
    }

    @GetMapping("shuffled-test")
    public TestDto getShuffledTest(@Param("id") int id) {
        return testService.getShuffledTest(id);
    }

    @PutMapping("test")
    public void updateTest(@Param("id") int id, @RequestBody TestDto testDto) {
        testService.updateTest(id, testDto);
    }

    @DeleteMapping("test")
    public void deleteTest(@Param("id") int id) {
        testService.deleteTest(id);
    }
}

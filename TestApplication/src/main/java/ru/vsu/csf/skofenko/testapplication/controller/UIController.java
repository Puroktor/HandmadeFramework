package ru.vsu.csf.skofenko.testapplication.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.framework.frontend.Nullable;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;

import java.util.List;

@Controller(value = "api", generateUI = true)
@DisplayName("Custom UI controller")
public class UIController {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @DisplayName("Custom dto")
    public static class Dto {
        @DisplayName("Custom number field description")
        private int number;
        @Nullable
        private String nullableText;
        @DisplayName("Custom boolean field description")
        private Boolean bool;
        @DisplayName("Custom enum field description")
        private Enum enumField;
        private List<Enum> listField;
        private InnerDto innerDto;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerDto {
        private int innerId;
        @DisplayName("nested class")
        private AnotherDto anotherDto;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnotherDto {
        private String usefulInfo;
        public Enum usefulEnum;
    }

    @DisplayName("Custom class description")
    public enum Enum {
        @DisplayName("Custom enum value 1")
        VALUE_1,

        @DisplayName("Second enum value")
        VALUE_2
    }

    @PostMapping("test")
    public Dto postTest(@Param("id") @DisplayName("custom id query param") int id, @RequestBody Dto dto) {
        return dto;
    }
    @DeleteMapping("test")
    public void delete() {
    }
}

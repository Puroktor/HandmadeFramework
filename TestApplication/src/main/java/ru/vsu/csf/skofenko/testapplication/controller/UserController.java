package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.ResponseStatus;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.testapplication.dto.UserDto;
import ru.vsu.csf.skofenko.testapplication.dto.UserRegistrationDto;
import ru.vsu.csf.skofenko.testapplication.service.UserService;

@Controller(value = "api/user")
public class UserController {
    @Inject
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.createUser(userRegistrationDto);
    }

    @GetMapping
    public UserDto getUser(@Param("id") int id) {
        return userService.getUser(id);
    }

    @PutMapping
    public boolean isUserGood(@Param("isGood") boolean isGood) {
        return isGood;
    }
}

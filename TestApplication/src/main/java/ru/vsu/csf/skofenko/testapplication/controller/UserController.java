package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.annotations.di.Controller;
import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.http.*;
import ru.vsu.csf.skofenko.testapplication.dto.UserDto;
import ru.vsu.csf.skofenko.testapplication.dto.UserRegistrationDto;
import ru.vsu.csf.skofenko.testapplication.service.UserService;

@Controller("api")
public class UserController {
    @Inject
    private UserService userService;

    @PostMapping("create-user")
    @ResponseType(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.createUser(userRegistrationDto);
    }

    @GetMapping("get-user")
    public UserDto getUser(@Param("id") int id) {
        return userService.getUser(id);
    }
}

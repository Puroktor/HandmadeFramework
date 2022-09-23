package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.UserDto;
import ru.vsu.csf.skofenko.testapplication.dto.UserRegistrationDto;
import ru.vsu.csf.skofenko.testapplication.entity.Role;
import ru.vsu.csf.skofenko.testapplication.entity.User;
import ru.vsu.csf.skofenko.testapplication.mapper.UserMapper;
import ru.vsu.csf.skofenko.testapplication.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
public class UserService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserMapper userMapper;

    public UserDto createUser(UserRegistrationDto userDto) {
        if (userDto.getRole() == Role.STUDENT && (userDto.getGroupNumber() == null || userDto.getYear() == null)) {
            throw new IllegalArgumentException("Student must have university year and group number!");
        } else if (userDto.getRole() == Role.TEACHER && (userDto.getGroupNumber() != null || userDto.getYear() != null)) {
            throw new IllegalArgumentException("Student must not have university year or group number!");
        } else if (userRepository.findByNickname(userDto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("User with such nickname already exists");
        }
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));
        return userMapper.toDto(user);
    }
}

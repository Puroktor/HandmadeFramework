package ru.vsu.csf.skofenko.testapplication.service;

import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.di.Service;
import ru.vsu.csf.framework.persistence.EntityManager;
import ru.vsu.csf.skofenko.testapplication.dto.UserDto;
import ru.vsu.csf.skofenko.testapplication.dto.UserRegistrationDto;
import ru.vsu.csf.skofenko.testapplication.entity.Role;
import ru.vsu.csf.skofenko.testapplication.entity.User;
import ru.vsu.csf.skofenko.testapplication.mapper.UserMapper;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UserService {
    @Inject
    private EntityManager entityManager;
    @Inject
    private UserMapper userMapper;

    public UserDto createUser(UserRegistrationDto userDto) {
        if (userDto.getRole() == Role.STUDENT && (userDto.getGroupNumber() == null || userDto.getYear() == null)) {
            throw new IllegalArgumentException("Student must have university year and group number!");
        } else if (userDto.getRole() == Role.TEACHER && (userDto.getGroupNumber() != null || userDto.getYear() != null)) {
            throw new IllegalArgumentException("Student must not have university year or group number!");
        } else if (!entityManager.findAllByProperties(User.class, Map.of("nickname", userDto.getNickname())).isEmpty()) {
            throw new IllegalArgumentException("User with such nickname already exists");
        }
        User user = userMapper.toEntity(userDto);
        entityManager.save(user);
        return userMapper.toDto(user);
    }

    public UserDto getUser(int id) {
        User user = entityManager.find(User.class, id)
                .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));
        return userMapper.toDto(user);
    }
}

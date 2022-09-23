package ru.vsu.csf.skofenko.testapplication.mapper;

import ru.vsu.csf.annotations.di.Service;
import ru.vsu.csf.skofenko.testapplication.dto.UserDto;
import ru.vsu.csf.skofenko.testapplication.dto.UserRegistrationDto;
import ru.vsu.csf.skofenko.testapplication.entity.User;

@Service
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getNickname(), user.getRole(), user.getUniversity(),
                user.getYear(), user.getGroupNumber(), user.getEmail());
    }

    public User toEntity(UserRegistrationDto dto) {
        return new User(null, dto.getName(), dto.getNickname(), null, dto.getRole(), dto.getUniversity(),
                dto.getYear(), dto.getGroupNumber(), dto.getEmail(), null);
    }
}

package ru.vsu.csf.skofenko.testapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.csf.skofenko.testapplication.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private String name;
    private String nickname;
    private String password;
    private Role role;
    private String university;
    private Integer year;
    private Integer groupNumber;
    private String email;
}

package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String nickname;
    private String password;
    private Role role;
    private String university;
    private Integer year;
    private Integer groupNumber;
    private String email;
    private List<Attempt> attempts;
}

package ru.vsu.csf.skofenko.testapplication.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum Role {
    TEACHER(new HashSet<>(Arrays.asList(Authorities.USER_SUBMIT, Authorities.USER_EDIT))),
    STUDENT(new HashSet<>(Arrays.asList(Authorities.USER_SUBMIT)));

    private final Set<Authorities> myAuthorities;
}

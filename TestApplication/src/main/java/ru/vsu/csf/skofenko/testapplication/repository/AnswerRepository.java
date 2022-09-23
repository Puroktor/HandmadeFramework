package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.annotations.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class AnswerRepository {
    private final Map<Integer, Answer> map = new HashMap<>();

    public Optional<Answer> findById(int id) {
        return Optional.ofNullable(map.get(id));
    }
}

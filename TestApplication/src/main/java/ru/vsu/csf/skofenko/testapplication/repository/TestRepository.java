package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TestRepository {
    private final Map<Integer, Test> map = new HashMap<>();
    private int generatedId = 1;

    public Test save(Test test) {
        if (test.getId() == null) {
            test.setId(generatedId++);
        }
        map.put(test.getId(), test);
        return test;
    }

    public Optional<Test> findById(int id) {
        return Optional.ofNullable(map.get(id));
    }

    public void delete(Test test) {
        map.remove(test.getId());
    }

    public void delete(int id) {
        map.remove(id);
    }
}

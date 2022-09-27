package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class QuestionRepository implements CrudRepository<Question, Integer> {
    private final Map<Integer, Question> map = new HashMap<>();
    private int generatedId = 1;

    @Override
    public Question save(Question entity) {
        if (entity.getId() == null) {
            entity.setId(generatedId++);
        }
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Question> findById(Integer integer) {
        return Optional.ofNullable(map.get(integer));
    }

    @Override
    public List<Question> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void delete(Question entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        map.remove(integer);
    }
}

package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Attempt;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import java.util.*;

@Repository
public class AttemptRepository implements CrudRepository<Attempt, Integer> {
    private final Map<Integer, Attempt> map = new HashMap<>();
    private int generatedId = 1;


    public List<Attempt> findAllByUserOrderByDateTimeDesc(User user) {
        List<Attempt> attempts = findAll();
        attempts.sort(Comparator.comparing(Attempt::getDateTime).reversed());
        return attempts;
    }

    @Override
    public Attempt save(Attempt entity) {
        if (entity.getId() == null) {
            entity.setId(generatedId++);
        }
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Attempt> findById(Integer integer) {
        return Optional.ofNullable(map.get(integer));
    }

    @Override
    public List<Attempt> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void delete(Attempt entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        map.remove(integer);
    }
}

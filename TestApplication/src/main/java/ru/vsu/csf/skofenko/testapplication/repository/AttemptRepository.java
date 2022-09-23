package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.Attempt;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import java.util.*;

@Repository
public class AttemptRepository {
    private final Map<Integer, Attempt> map = new HashMap<>();
    private int generatedId = 1;

    public Attempt save(Attempt attempt) {
        if (attempt.getId() == null) {
            attempt.setId(generatedId++);
        }
        map.put(attempt.getId(), attempt);
        return attempt;
    }

    public Optional<Attempt> findById(int id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<Attempt> findAllByUserOrderByDateTimeDesc(User user) {
        List<Attempt> attempts = new ArrayList<>();
        for (Attempt attempt : map.values()) {
            if(attempt.getUser().equals(user)){
                attempts.add(attempt);
            }
        }
        attempts.sort(Comparator.comparing(Attempt::getDateTime).reversed());
        return attempts;
    }
}

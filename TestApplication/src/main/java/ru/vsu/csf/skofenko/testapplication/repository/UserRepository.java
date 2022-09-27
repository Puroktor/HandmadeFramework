package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.framework.persistence.CrudRepository;
import ru.vsu.csf.skofenko.testapplication.entity.Role;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import java.util.*;

@Repository
public class UserRepository implements CrudRepository<User, Integer> {
    private final Map<Integer, User> map = new HashMap<>();
    private int generatedId = 3;

    {
        map.put(1, new User(1, "Student Student", "Student", "Student",
                Role.STUDENT, "University", 1, 1, "student@gmail.com", new ArrayList<>()));
        map.put(2, new User(2, "Teacher Teacher", "Teacher", "Teacher",
                Role.TEACHER, "University", null, null, "teacher@gmail.com", new ArrayList<>()));
    }

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.setId(generatedId++);
        }
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(Integer integer) {
        return Optional.ofNullable(map.get(integer));
    }

    @Override
    public List<User> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void delete(User entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Integer integer) {
        map.remove(integer);
    }

    public Optional<User> findByNickname(String nickname) {
        return findAll().stream()
                .filter(user -> user.getNickname().equals(nickname))
                .findFirst();
    }
}

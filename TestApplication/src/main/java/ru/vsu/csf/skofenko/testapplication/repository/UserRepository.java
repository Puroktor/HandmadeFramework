package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.framework.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.Role;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Integer, User> map = new HashMap<>();
    private int generatedId = 3;

    {
        map.put(1, new User(1, "Student Student", "Student", "Student",
                Role.STUDENT, "University", 1, 1, "student@gmail.com", new ArrayList<>()));
        map.put(2, new User(2, "Teacher Teacher", "Teacher", "Teacher",
                Role.TEACHER, "University", null, null, "teacher@gmail.com", new ArrayList<>()));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(generatedId++);
        }
        map.put(user.getId(), user);
        return user;
    }

    public Optional<User> findByNickname(String nickname) {
        User foundUser = null;
        for (User user : map.values()) {
            if (user.getNickname().equals(nickname)) {
                foundUser = user;
                break;
            }
        }
        return Optional.ofNullable(foundUser);
    }

    public Optional<User> findById(int userId) {
        return Optional.ofNullable(map.get(userId));
    }
}

package ru.vsu.csf.skofenko.testapplication.repository;

import ru.vsu.csf.annotations.di.Repository;
import ru.vsu.csf.skofenko.testapplication.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Integer, User> map = new HashMap<>();
    private int generatedId = 1;

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

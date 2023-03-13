package ru.practicum.shareit.user.repo;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InMemoryUserRepo implements UserRepo {
    Map<Integer, User> userMap = new HashMap<>();
    @NonFinal
    int userId = 0;

    @Override
    public User addUser(User user) {
        ++userId;
        user.setId(userId);
        userMap.put(user.getId(), user);
        log.info("User {} added successfully", user);
        return userMap.get(userId);
    }

    @Override
    public User updateUserName(User user) {
        userMap.get(user.getId()).setName(user.getName());
        User updatedUser = userMap.get(user.getId());
        log.info("Updated name for user: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User updateUserEmail(User user) {
        userMap.get(user.getId()).setEmail(user.getEmail());
        User updatedUser = userMap.get(user.getId());
        log.info("Updated email for user: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User getUserById(int userId) {
        return userMap.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteUserById(int userId) {
        userMap.remove(userId);
        log.info("Deleted user with id = {}", userId);
    }

    @Override
    public List<String> getUserEmails() {
        return userMap.values().stream()
                .map(User::getEmail).collect(Collectors.toList());
    }
}

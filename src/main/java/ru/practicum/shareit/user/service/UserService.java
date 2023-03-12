package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(int userId, User user);

    User getUserById(int userId);

    List<User> getAllUsers();

    void deleteUserById(int userId);
}

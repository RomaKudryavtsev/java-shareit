package ru.practicum.shareit.user.repo;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepo {
    User addUser(User user);

    User updateUserName(User user);

    User updateUserEmail(User user);

    User getUserById(int userId);

    List<User> getAllUsers();

    void deleteUserById(int userId);

    List<String> getUserEmails();
}

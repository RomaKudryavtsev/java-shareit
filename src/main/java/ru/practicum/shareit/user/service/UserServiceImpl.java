package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyInUseException;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private void checkIfEmailIsDuplicative(String email) {
        if (userRepo.getUserEmails().contains(email)) {
            throw new EmailAlreadyInUseException("Email already exists");
        }
    }

    private boolean checkIfUserTriesToUpdateWithSameEmail(int userId, User user) {
        return userRepo.getUserById(userId).getEmail().equals(user.getEmail());
    }

    @Override
    public User addUser(User user) {
        if (user.getEmail() == null) {
            throw new EmptyEmailException("Email is empty");
        }
        checkIfEmailIsDuplicative(user.getEmail());
        return userRepo.addUser(user);
    }

    @Override
    public User updateUser(int userId, User user) {
        user.setId(userId);
        if (user.getName() != null) {
            userRepo.updateUserName(user);
        }
        if (user.getEmail() != null) {
            if (!checkIfUserTriesToUpdateWithSameEmail(userId, user)) {
                checkIfEmailIsDuplicative(user.getEmail());
                userRepo.updateUserEmail(user);
            } else {
                log.info("User {} tries to update with the same email", userId);
            }
        }
        return userRepo.getUserById(userId);
    }

    @Override
    public User getUserById(int userId) {
        return userRepo.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    @Override
    public void deleteUserById(int userId) {
        userRepo.deleteUserById(userId);
    }
}

package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
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

    private boolean checkIfUserTriesToUpdateWithSameEmail(Long userId, User user) {
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(String.format("User %d does not exist", userId));
                })
                .getEmail().equals(user.getEmail());
    }

    @Transactional
    @Override
    public User addUser(User user) {
        if (user.getEmail() == null) {
            throw new EmptyEmailException("Email is empty");
        }
        return userRepo.save(user);
    }

    @Transactional
    @Override
    public User updateUser(Long userId, User user) {
        user.setId(userId);
        if (user.getName() != null) {
            userRepo.updateUserName(user.getId(), user.getName());
        }
        if (user.getEmail() != null) {
            if (!checkIfUserTriesToUpdateWithSameEmail(userId, user)) {
                userRepo.updateUserEmail(user.getId(), user.getEmail());
            } else {
                log.info("User {} tries to update with the same email", userId);
            }
        }
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(String.format("User %d does not exist", userId));
                });
    }

    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(String.format("User %d does not exist", userId));
                });
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepo.deleteById(userId);
    }
}

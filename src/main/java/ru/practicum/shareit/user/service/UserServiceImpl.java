package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
        if (userRepo.findAll().stream().map(User::getEmail).anyMatch(s -> s.equals(email))) {
            throw new EmailAlreadyInUseException("Email already exists");
        }
    }

    private boolean checkIfUserTriesToUpdateWithSameEmail(Long userId, User user) {
        return userRepo.findById(userId).get().getEmail().equals(user.getEmail());
    }

    @Transactional
    @Override
    public User addUser(User user) {
        if (user.getEmail() == null) {
            throw new EmptyEmailException("Email is empty");
        }
        checkIfEmailIsDuplicative(user.getEmail());
        return userRepo.save(user);
    }

    @Transactional
    @Override
    public User updateUser(Long userId, User user) {
        user.setId(userId);
        if (user.getName() != null) {
            userRepo.updateUserName(user.getId(), user.getName());
            log.info("Username updated");
        }
        if (user.getEmail() != null) {
            if (!checkIfUserTriesToUpdateWithSameEmail(userId, user)) {
                checkIfEmailIsDuplicative(user.getEmail());
                userRepo.updateUserEmail(user.getId(), user.getEmail());
                log.info("Email updated");
            } else {
                log.info("User {} tries to update with the same email", userId);
            }
        }
        User userUpdated = userRepo.findById(userId).get();
        log.info("Updated: {}", userUpdated);
        log.info("Email updated: {}", userUpdated.getEmail());
        return userUpdated;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId).get();
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

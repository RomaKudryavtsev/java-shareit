package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final static String ALTER_USER_PATH = "/{id}";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PatchMapping(value = ALTER_USER_PATH)
    public User updateUser(@PathVariable("id") int userId, @Valid @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @GetMapping(ALTER_USER_PATH)
    public User getUserById(@PathVariable("id") int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(value = ALTER_USER_PATH)
    public void deleteUserById(@PathVariable("id") int userId) {
        userService.deleteUserById(userId);
    }
}

package ru.practicum.shareit.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    @Modifying
    @Query("update User u set u.name = ?2 where u.id = ?1")
    void updateUserName(Long id, String name);

    @Modifying
    @Query("update User u set u.email = ?2 where u.id = ?1")
    void updateUserEmail(Long id, String email);
}

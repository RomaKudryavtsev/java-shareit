package ru.practicum.shareit.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.projection.Booker;

import javax.persistence.LockModeType;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update User u set u.name = ?2 where u.id = ?1")
    void updateUserName(Long id, String name);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update User u set u.email = ?2 where u.id = ?1")
    void updateUserEmail(Long id, String email);
    @Query("select new ru.practicum.shareit.user.projection.Booker(it.id) from Item as it")
    Booker findBookerById(long id);
}

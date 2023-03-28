package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.available = ?2 where i.id = ?1")
    void updateAvailable(Long id, Boolean available);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.description = ?2 where i.id = ?1")
    void updateDescription(Long id, String description);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.name = ?2 where i.id = ?1")
    void updateName(Long id, String name);

    List<Item> findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String description, String name);
}

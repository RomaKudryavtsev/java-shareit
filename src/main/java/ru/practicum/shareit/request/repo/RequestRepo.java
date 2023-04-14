package ru.practicum.shareit.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.projection.ItemRequestWithItems;

import java.util.List;

public interface RequestRepo extends JpaRepository<ItemRequest, Long> {
    List<ItemRequestWithItems> findAllByUser_Id(Long ownerId);
    ItemRequestWithItems findAllById(Long requestId);
}

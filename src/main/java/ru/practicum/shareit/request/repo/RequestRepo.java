package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.projection.ItemRequestWithItems;

import java.util.List;

public interface RequestRepo extends JpaRepository<ItemRequest, Long> {
    List<ItemRequestWithItems> findAllByUser_Id(Long ownerId);

    Page<ItemRequestWithItems> findAllByUser_IdNot(Long userId, Pageable page);

    ItemRequestWithItems findAllById(Long requestId);
}

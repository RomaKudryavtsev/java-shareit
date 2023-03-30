package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBooking;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemWithLastAndNextBooking getItemById(Long userId, Long itemId);

    List<ItemWithLastAndNextBooking> getAllOwnersItems(Long ownerId);

    List<ItemDto> searchItems(String text);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllOwnersItems(Long ownerId);

    List<ItemDto> searchItems(String text);
}

package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getUsersRequestsWithItems(Long userId);

    ItemRequestWithItemsDto getRequestByIdWithItems(Long userId, Long requestId);
}

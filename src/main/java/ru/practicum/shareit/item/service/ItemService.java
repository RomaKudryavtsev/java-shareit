package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentsRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemWithLastAndNextBookingAndComments getItemById(Long userId, Long itemId);

    List<ItemWithLastAndNextBookingAndComments> getAllOwnersItems(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentWithAuthorName addComment(Long userId, Long itemId, CommentsRequestDto commentsRequestDto);
}

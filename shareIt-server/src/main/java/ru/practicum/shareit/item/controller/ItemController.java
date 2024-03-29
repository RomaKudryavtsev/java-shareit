package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String SPECIFIC_ITEM_PATH = "/{id}";
    private static final String SEARCH_PATH = "/search";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping(value = SPECIFIC_ITEM_PATH + "/comment")
    public CommentWithAuthorName addComment(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long itemId,
                                            @RequestBody CommentsDto commentsDto) {
        return itemService.addComment(userId, itemId, commentsDto);
    }

    @PatchMapping(value = SPECIFIC_ITEM_PATH)
    public ItemDto updateItem(@RequestHeader(USER_HEADER) Long ownerId, @PathVariable("id") Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping(SPECIFIC_ITEM_PATH)
    public ItemWithLastAndNextBookingAndComments getItemById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithLastAndNextBookingAndComments> getOwnersItems(@RequestHeader(USER_HEADER) Long ownerId) {
        return itemService.getAllOwnersItems(ownerId);
    }

    @GetMapping(SEARCH_PATH)
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}

package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final static String SPECIFIC_ITEM_PATH = "/{id}";
    private final static String SEARCH_PATH = "/search";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping(value = SPECIFIC_ITEM_PATH)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable("id") int itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping(SPECIFIC_ITEM_PATH)
    public ItemDto getItemById(@PathVariable("id") int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getAllOwnersItems(ownerId);
    }

    @GetMapping(SEARCH_PATH)
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
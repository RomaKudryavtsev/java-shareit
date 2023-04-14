package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return requestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getUsersRequestsWithItems(@RequestHeader(USER_HEADER) Long userId) {
        return requestService.getUsersRequestsWithItems(userId);
    }
}

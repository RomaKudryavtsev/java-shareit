package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequest mapDtoToModel(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        return request;
    }

    public static ItemRequestDto mapModelToDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }
}

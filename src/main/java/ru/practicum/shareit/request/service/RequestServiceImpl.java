package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepo requestRepo;
    private final UserRepo userRepo;

    @Autowired
    public RequestServiceImpl(RequestRepo requestRepo, UserRepo userRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
    }

    private void checkIfUserExists(Long userId) {
        userRepo.findById(userId).orElseThrow(() -> {throw new UserNotFoundException("User does not exist");});
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        ItemRequest newRequest = ItemRequestMapper.mapDtoToModel(itemRequestDto);
        newRequest.setCreated(now);
        newRequest.setUser(userRepo.findById(userId)
                .orElseThrow(() -> {throw new UserNotFoundException("User does not exist");}));
        ItemRequest addedRequest = requestRepo.save(newRequest);
        return ItemRequestMapper.mapModelToDto(addedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getUsersRequestsWithItems(Long userId) {
        checkIfUserExists(userId);
        return requestRepo.findAllByUser_Id(userId).stream()
                .map(ItemRequestMapper::mapModelToDtoWithItems)
                .collect(Collectors.toList());
    }
}

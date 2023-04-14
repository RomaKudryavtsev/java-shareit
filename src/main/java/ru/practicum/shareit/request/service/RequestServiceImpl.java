package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
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
        userRepo.findById(userId)
                .orElseThrow(() -> {throw new UserNotFoundException("User does not exist");});
    }

    private void checkIfRequestExists(Long requestId) {
        requestRepo.findById(requestId)
                .orElseThrow(() -> {throw new RequestNotFoundException("Request does not exist");});
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

    @Override
    public ItemRequestWithItemsDto getRequestByIdWithItems(Long userId, Long requestId) {
        checkIfUserExists(userId);
        checkIfRequestExists(requestId);
        return ItemRequestMapper.mapModelToDtoWithItems(requestRepo.findAllById(requestId));
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllRequestsOfOtherUsers(Long userId, int from, int size) {
        checkIfUserExists(userId);
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        return requestRepo.findAllByUser_IdNot(userId, request).getContent().stream()
                .map(ItemRequestMapper::mapModelToDtoWithItems)
                .collect(Collectors.toList());
    }
}

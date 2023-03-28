package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.repo.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepo itemRepo;
    private final UserRepo userRepo;

    @Autowired
    public ItemServiceImpl(ItemRepo itemRepo, UserRepo userRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    private void checkIfUserIsOwner(Long ownerId, Long itemId) {
        if (!itemRepo.findById(itemId).get().getOwnerId().equals(ownerId)) {
            throw new NonOwnerUpdatingException("Item can be updated only by its owner");
        }
    }

    private void checkIfUserExists(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkIfUserExists(userId);
        if (itemDto.getAvailable() == null) {
            throw new EmptyItemAvailabilityException("Item availability is empty");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new EmptyItemNameException("Item name is empty");
        }
        if (itemDto.getDescription() == null) {
            throw new EmptyItemDescriptionException("Item description is empty");
        }
        Item inputItem = ItemMapper.mapToModel(itemDto);
        inputItem.setOwnerId(userId);
        Item addedItem = itemRepo.save(inputItem);
        return ItemMapper.mapToDto(addedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        checkIfUserIsOwner(ownerId, itemId);
        Item inputItem = ItemMapper.mapToModel(itemDto);
        inputItem.setId(itemId);
        inputItem.setOwnerId(ownerId);
        if (inputItem.getAvailable() != null) {
            itemRepo.updateAvailable(inputItem.getId(), inputItem.getAvailable());
        }
        if (inputItem.getDescription() != null) {
            itemRepo.updateDescription(inputItem.getId(), inputItem.getDescription());
        }
        if (inputItem.getName() != null) {
            itemRepo.updateName(inputItem.getId(), inputItem.getName());
        }
        return ItemMapper.mapToDto(itemRepo.findById(itemId).get());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.mapToDto(itemRepo.findById(itemId).get());
    }

    @Override
    public List<ItemDto> getAllOwnersItems(Long ownerId) {
        return itemRepo.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        String searchableText = text.toLowerCase();
        return itemRepo.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(searchableText,
                        searchableText).stream()
                .map(ItemMapper::mapToDto).collect(Collectors.toList());
    }
}

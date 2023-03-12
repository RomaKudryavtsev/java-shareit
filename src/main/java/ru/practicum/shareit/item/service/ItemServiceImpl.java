package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private void checkIfUserIsOwner(int ownerId, int itemId) {
        if(itemRepo.getItemById(itemId).getOwnerId() != ownerId) {
            throw new NonOwnerUpdatingException("Item can be updated only by its owner");
        }
    }

    private void checkIfUserExists(int userId) {
        if(userRepo.getUserById(userId) == null) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        checkIfUserExists(userId);
        if(itemDto.getAvailable() == null) {
            throw new EmptyItemAvailabilityException("Item availability is empty");
        }
        if(itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new EmptyItemNameException("Item name is empty");
        }
        if(itemDto.getDescription() == null) {
            throw new EmptyItemDescriptionException("Item description is empty");
        }
        Item inputItem = ItemMapper.mapToModel(itemDto);
        inputItem.setOwnerId(userId);
        Item addedItem = itemRepo.addItem(inputItem);
        return ItemMapper.mapToDto(addedItem);
    }

    @Override
    public ItemDto updateItem(int ownerId, int itemId, ItemDto itemDto) {
        checkIfUserIsOwner(ownerId, itemId);
        Item inputItem = ItemMapper.mapToModel(itemDto);
        inputItem.setId(itemId);
        inputItem.setOwnerId(ownerId);
        if(inputItem.getAvailable() != null) {
            itemRepo.updateAvailable(inputItem);
        }
        if(inputItem.getDescription() != null) {
            itemRepo.updateDescription(inputItem);
        }
        if(inputItem.getName() != null) {
            itemRepo.updateName(inputItem);
        }
        return ItemMapper.mapToDto(itemRepo.getItemById(itemId));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.mapToDto(itemRepo.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllOwnersItems(int ownerId) {
        return itemRepo.getListOfOwnersItems(ownerId).stream()
                .map(ItemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if(text.isEmpty()) {
            return List.of();
        }
        String searchableText = text.toLowerCase();
        return itemRepo.searchItems(searchableText).stream()
                .map(ItemMapper::mapToDto).collect(Collectors.toList());
    }
}

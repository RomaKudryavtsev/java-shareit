package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentsRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBooking;
import ru.practicum.shareit.item.repo.CommentRepo;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final CommentRepo commentRepo;
    private final Function<Instant, Predicate<Booking>> notFutureBookingsFunction = now ->
            b -> b.getStart().isBefore(now);

    @Autowired
    public ItemServiceImpl(ItemRepo itemRepo, UserRepo userRepo, CommentRepo commentRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.commentRepo = commentRepo;
    }

    private void checkIfUserIsOwner(Long ownerId, Long itemId) {
        if (!itemRepo.findById(itemId).orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");})
                .getOwnerId().equals(ownerId)) {
            throw new NonOwnerUpdatingException("Item can be updated only by its owner");
        }
    }

    private void checkIfUserExists(Long userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void checkIfCommentRelatedToCurrentBooking(Long userId, Long itemId, Instant now) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");});
        List<Booking> bookings = item.getBookings();
        if (bookings.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> notFutureBookingsFunction.apply(now).test(b))
                .noneMatch(b -> b.getBooker().getId().equals(userId))) {
            throw new IllegalCommentException("Comment is related to future booking or user is not a booker");
        }
    }

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new EmptyItemAvailabilityException("Item availability is empty");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new EmptyItemNameException("Item name is empty");
        }
        if (itemDto.getDescription() == null) {
            throw new EmptyItemDescriptionException("Item description is empty");
        }
        checkIfUserExists(userId);
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
        return ItemMapper.mapToDto(itemRepo.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");}));
    }

    @Override
    public ItemWithLastAndNextBooking getItemById(Long userId, Long itemId) {
        boolean isOwner;
        try {
            checkIfUserIsOwner(userId, itemId);
            isOwner = true;
        } catch (NonOwnerUpdatingException e) {
            isOwner = false;
        }
        Instant now = Instant.now();
        return itemRepo.findItemWithLastAndNextBooking(itemId, now, isOwner);
    }

    @Override
    public List<ItemWithLastAndNextBooking> getAllOwnersItems(Long ownerId) {
        Instant now = Instant.now();
        return itemRepo.findAllWithLastAndNextBooking(ownerId, now);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        String searchableText = text.toLowerCase();
        return itemRepo.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(searchableText,
                        searchableText).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentWithAuthorName addComment(Long userId, Long itemId, CommentsRequestDto commentsRequestDto) {
        Instant now = Instant.now();
        checkIfCommentRelatedToCurrentBooking(userId, itemId, now);
        Comment newComment = CommentMapper.mapDtoToModel(commentsRequestDto);
        newComment.setItem(itemRepo.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");}));
        newComment.setAuthor(userRepo.findById(userId)
                .orElseThrow(() -> {throw new UserNotFoundException("User does not exist");}));
        newComment.setCreated(now);
        Comment addedComment = commentRepo.save(newComment);
        return commentRepo.findWithAuthorName(addedComment.getId());
    }
}

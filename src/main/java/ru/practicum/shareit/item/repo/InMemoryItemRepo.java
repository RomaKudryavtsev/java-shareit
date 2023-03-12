package ru.practicum.shareit.item.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryItemRepo implements ItemRepo {
    private final Map<Integer, Item> itemMap = new HashMap<>();
    private int itemId = 0;

    @Override
    public Item addItem(Item item) {
        ++itemId;
        item.setId(itemId);
        itemMap.put(item.getId(), item);
        log.info("Item {} was added successfully", item);
        return itemMap.get(itemId);
    }

    @Override
    public Item getItemById(int itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> getListOfOwnersItems(int ownerId) {
        return itemMap.values().stream().filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateAvailable(Item inputItem) {
        itemMap.get(inputItem.getId()).setAvailable(inputItem.getAvailable());
        Item updatedItem = itemMap.get(inputItem.getId());
        log.info("Updated availability for item: {}", updatedItem);
        return updatedItem;
    }

    @Override
    public Item updateDescription(Item inputItem) {
        itemMap.get(inputItem.getId()).setDescription(inputItem.getDescription());
        Item updatedItem = itemMap.get(inputItem.getId());
        log.info("Updated description for item: {}", updatedItem);
        return updatedItem;
    }

    @Override
    public Item updateName(Item inputItem) {
        itemMap.get(inputItem.getId()).setName(inputItem.getName());
        Item updatedItem = itemMap.get(inputItem.getId());
        log.info("Updated name for item: {}", updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }
}

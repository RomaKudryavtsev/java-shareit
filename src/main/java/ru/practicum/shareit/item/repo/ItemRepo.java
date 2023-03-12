package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepo {
    Item addItem(Item item);

    Item getItemById(int itemId);

    List<Item> getListOfOwnersItems(int ownerId);

    Item updateAvailable(Item inputItem);

    Item updateDescription(Item inputItem);

    Item updateName(Item inputItem);

    List<Item> searchItems(String text);
}

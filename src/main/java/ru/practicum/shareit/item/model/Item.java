package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    int id;
    int ownerId;
    String name;
    String description;
    Boolean available;

    public Boolean getAvailable() {
        return available;
    }
}

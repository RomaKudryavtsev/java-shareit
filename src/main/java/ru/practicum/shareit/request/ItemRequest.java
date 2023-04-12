package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "description")
    String description;
    @Column(name = "created")
    LocalDateTime created;
    @OneToMany(
            targetEntity = Item.class,
            mappedBy = "request",
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    List<Item> items;

}

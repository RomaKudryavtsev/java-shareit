package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name = "bookings")
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start")
    Instant start;
    @Column(name = "fin")
    Instant end;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "booker_id")
    long bookerId;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    Long itemId;
}

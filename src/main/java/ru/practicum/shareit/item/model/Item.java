package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items", schema = "public")
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "owner_id")
    Long ownerId;
    @Column(name = "name")
    String name;
    @Column(name = "description")
    String description;
    @Column(name = "available")
    Boolean available;

    @OneToMany(
            targetEntity = Booking.class,
            mappedBy = "item",
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<Booking> bookings;

    public Boolean getAvailable() {
        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

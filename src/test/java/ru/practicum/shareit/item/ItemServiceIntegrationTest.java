package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class ItemServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private BookingService bookingService;
    private ItemDto addedItem;
    private User addedOwner;
    private BookingResponseDto approvedBooking;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("John Sharer");
        owner.setEmail("js@gmail.com");
        addedOwner = userService.addUser(owner);
        User booker = new User();
        booker.setName("James Booker");
        booker.setEmail("jb@gmail.com");
        User addedBooker = userService.addUser(booker);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Request")
                .build();
        ItemRequestDto addedRequest = requestService.addItemRequest(addedBooker.getId(), requestDto);

        ItemDto itemDto = ItemDto.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(addedRequest.getId())
                .build();
        addedItem = itemService.addItem(addedOwner.getId(), itemDto);

        LocalDateTime nextStart = LocalDateTime.now().plusDays(1);
        LocalDateTime nextEnd = nextStart.plusDays(2);
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setStart(nextStart);
        bookingDto.setEnd(nextEnd);
        bookingDto.setItemId(addedItem.getId());
        BookingResponseDto addedBooking = bookingService.addBooking(addedBooker.getId(), bookingDto);
        approvedBooking = bookingService
                .setBookingStatus(addedOwner.getId(), addedBooking.getId(), true);
    }

    @Test
    void testFindItems() {
        ItemWithLastAndNextBookingAndComments itemFull = itemService.getItemById(addedOwner.getId(), addedItem.getId());
        assertThat(itemFull.getNextBooking().getId(), equalTo(approvedBooking.getId()));
        assertThat(itemFull.getNextBooking().getBookerId(), equalTo(approvedBooking.getBooker().getId()));
        assertNull(itemFull.getLastBooking());
        assertThat(itemFull.getComments(), hasSize(0));
        List<ItemWithLastAndNextBookingAndComments> itemFullAll = itemService.getAllOwnersItems(addedOwner.getId());
        assertThat(itemFullAll, hasSize(1));
        assertThat(itemFullAll.get(0).getNextBooking().getId(), equalTo(approvedBooking.getId()));
        assertThat(itemFullAll.get(0).getNextBooking().getBookerId(), equalTo(approvedBooking.getBooker().getId()));
        assertNull(itemFullAll.get(0).getLastBooking());
        assertThat(itemFullAll.get(0).getComments(), hasSize(0));
    }
}

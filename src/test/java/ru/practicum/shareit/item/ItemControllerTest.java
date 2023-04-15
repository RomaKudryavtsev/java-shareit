package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.projection.BookingShortForItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;
    ItemDto item;
    CommentWithAuthorName comment;
    ItemWithLastAndNextBookingAndComments itemWithInfo;
    LocalDateTime testNow = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        item = ItemDto.builder()
                .id(1L)
                .name("New item")
                .description("Item description")
                .available(true)
                .requestId(1L)
                .build();
        comment = new CommentWithAuthorName(1L, "Comment", "Author", testNow);
        itemWithInfo = ItemWithLastAndNextBookingAndComments.builder()
                .id(1L)
                .ownerId(1L)
                .name("New item")
                .description("Item description")
                .available(true)
                .nextBooking(new BookingShortForItem(1L, 1L))
                .lastBooking(new BookingShortForItem(2L, 1L))
                .comments(List.of(comment))
                .build();
    }

    @Test
    void testAddItem() throws Exception {
        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any())).thenReturn(item);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId().intValue())));
    }

    @Test
    void testAddComment() throws Exception {
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())));
    }

    @Test
    void testUpdateItem() throws Exception {
        item.setDescription("Updated description");
        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId().intValue())));
    }

    @Test
    void testGetItemById() throws Exception {
        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemWithInfo);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithInfo.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithInfo.getName())))
                .andExpect(jsonPath("$.description", is(itemWithInfo.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithInfo.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemWithInfo.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemWithInfo.getLastBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemWithInfo.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemWithInfo.getNextBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(comment.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(comment.getAuthorName())));
    }

    @Test
    void testGetOwnersItems() throws Exception {
        Mockito.when(itemService.getAllOwnersItems(Mockito.anyLong())).thenReturn(List.of(itemWithInfo));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithInfo.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithInfo.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithInfo.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithInfo.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemWithInfo.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemWithInfo.getLastBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemWithInfo.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemWithInfo.getNextBooking().getBookerId().intValue())))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(comment.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(comment.getAuthorName())));
    }

    @Test
    void testSearchItems() throws Exception {
        Mockito.when(itemService.searchItems(Mockito.anyString())).thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(item.getRequestId().intValue())));
    }
}

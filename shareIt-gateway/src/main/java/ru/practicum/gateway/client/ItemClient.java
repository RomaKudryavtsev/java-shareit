package ru.practicum.gateway.client;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.dto.CommentWithAuthorName;
import ru.practicum.gateway.dto.CommentsDto;
import ru.practicum.gateway.dto.ItemDto;
import ru.practicum.gateway.dto.ItemWithLastAndNextBookingAndComments;
import ru.practicum.gateway.exception.MonoException;

import java.util.List;

@Service
public class ItemClient extends BaseClient {
    private final static String ITEM_URI = "/items/";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public Mono<ItemDto> addItem(Long userId, ItemDto itemDto) {
        return webClient.post()
                .uri(baseUrl + ITEM_URI)
                .bodyValue(itemDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemDto.class);
                    }
                });
    }

    public Mono<CommentWithAuthorName> addComment(Long userId, Long itemId, CommentsDto commentsDto) {
        return webClient.post()
                .uri(baseUrl + ITEM_URI + itemId + "/comments")
                .bodyValue(commentsDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(CommentWithAuthorName.class);
                    }
                });
    }

    public Mono<ItemDto> updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        return webClient.patch()
                .uri(baseUrl + ITEM_URI + itemId)
                .bodyValue(itemDto)
                .header(USER_HEADER, ownerId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemDto.class);
                    }
                });
    }

    public Mono<ItemWithLastAndNextBookingAndComments> getItemById(Long userId, Long itemId) {
        return webClient.get()
                .uri(baseUrl + ITEM_URI + itemId)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemWithLastAndNextBookingAndComments.class);
                    }
                });
    }

    public Mono<List<ItemWithLastAndNextBookingAndComments>> getAllOwnersItems(Long ownerId) {
        return webClient.get()
                .uri(baseUrl + ITEM_URI)
                .header(USER_HEADER, ownerId.toString())
                .retrieve().bodyToFlux(ItemWithLastAndNextBookingAndComments.class).collectList();
    }

    public Mono<List<ItemDto>> searchItems(String text) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + ITEM_URI + "search")
                        .queryParam("text", text)
                        .build())
                .retrieve().bodyToFlux(ItemDto.class).collectList();
    }
}

package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentsRequestDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment mapDtoToModel(CommentsRequestDto commentsRequestDto) {
        Comment comment = new Comment();
        comment.setText(commentsRequestDto.getText());
        return comment;
    }
}

package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentsDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment mapDtoToModel(CommentsDto commentsDto) {
        Comment comment = new Comment();
        comment.setText(commentsDto.getText());
        return comment;
    }
}

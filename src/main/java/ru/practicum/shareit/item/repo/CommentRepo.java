package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.projection.CommentWithAuthorName(c.id, c.text, c.author.name, " +
            "c.created) from Comment c where c.id = ?1")
    CommentWithAuthorName findWithAuthorName(Long commentId);
}

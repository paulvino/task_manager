package io.project.repository;

import io.project.model.Comment;
import io.project.model.Task;
import io.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask(Task task);
    List<Comment> findByAuthor(User author);
    List<Comment> findByIdIn(List<Long> id);
}

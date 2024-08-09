package io.project.service;

import io.project.dto.commentDto.CommentCreateDTO;
import io.project.dto.commentDto.CommentDTO;
import io.project.dto.commentDto.CommentUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.mapper.CommentMapper;
import io.project.repository.CommentRepository;
import io.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final TaskRepository taskRepository;

    public List<CommentDTO> getAll() {
        var comment = commentRepository.findAll();
        return comment.stream()
                .map(commentMapper::map)
                .toList();
    }

    public CommentDTO findById(Long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
        return commentMapper.map(comment);
    }

    public CommentDTO create(CommentCreateDTO commentData) {
//        if (commentData.getTaskId() == null) {
//            throw new IllegalArgumentException("Task information must be provided.");
//        }

        var comment = commentMapper.map(commentData);

        var currentUser = userService.getCurrentUser();
        comment.setAuthor(currentUser);

        var taskId = commentData.getTaskId();

        var currentTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        comment.setTask(currentTask);

        commentRepository.save(comment);

        return commentMapper.map(comment);
    }

    public CommentDTO update(CommentUpdateDTO commentData, Long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));

        commentMapper.update(commentData, comment);
        commentRepository.save(comment);

        return commentMapper.map(comment);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}

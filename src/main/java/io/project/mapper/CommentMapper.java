package io.project.mapper;

import io.project.dto.commentDto.CommentCreateDTO;
import io.project.dto.commentDto.CommentDTO;
import io.project.dto.commentDto.CommentUpdateDTO;
import io.project.exception.ResourceNotFoundException;
import io.project.model.Comment;

import io.project.model.Task;
import io.project.repository.TaskRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CommentMapper {
    @Autowired
    private TaskRepository taskRepository;

    @Mapping(target = "task", expression = "java(findTask(model.getTaskId()))")
    public abstract Comment map(CommentCreateDTO model);
    public abstract CommentDTO map(Comment model);
    public abstract void update(CommentUpdateDTO update, @MappingTarget Comment destination);

    protected Task findTask(Long taskId) {
//        if (taskId == null) {
//            throw new IllegalArgumentException("Task ID cannot be null");
//        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
    }
}

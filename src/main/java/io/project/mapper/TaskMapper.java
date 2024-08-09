package io.project.mapper;

import io.project.dto.taskDto.TaskCreateDTO;
import io.project.dto.taskDto.TaskDTO;
import io.project.dto.taskDto.TaskUpdateDTO;

import io.project.exception.ResourceNotFoundException;
import io.project.model.Comment;
import io.project.model.Priority;
import io.project.model.Task;
import io.project.model.TaskStatus;
import io.project.repository.CommentRepository;
import io.project.repository.PriorityRepository;
import io.project.repository.TaskStatusRepository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private PriorityRepository priorityRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "priorityId", target = "priority")
    @Mapping(source = "commentIds", target = "commentIds")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "priority.id", target = "priorityId")
    @Mapping(source = "commentIds", target = "commentIds")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "priorityId", target = "priority")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task destination);

    public TaskStatus toEntity(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    }

    protected Long map(Priority priority) {
        return priority == null ? null : priority.getId();
    }

    protected Priority map(Long id) {
        return id == null ? null : priorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));
    }

    public List<Comment> toEntities(List<Long> commentIds) {
        return commentRepository.findByIdIn(commentIds);
    }

    public List<Long> toIds(Set<Comment> comments) {
        return comments == null
                ? null
                : comments.stream()
                .map(Comment::getId)
                .toList();
    }
}

package io.project.mapper;

import io.project.dto.taskDto.TaskCreateDTO;
import io.project.dto.taskDto.TaskDTO;
import io.project.dto.taskDto.TaskUpdateDTO;

import io.project.exception.ResourceNotFoundException;
import io.project.model.Task;
import io.project.model.TaskStatus;
import io.project.repository.TaskStatusRepository;

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
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "status", target = "taskStatus")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task destination);

    public TaskStatus toEntity(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    }
}

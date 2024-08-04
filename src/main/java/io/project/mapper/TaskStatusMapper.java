package io.project.mapper;

import io.project.model.TaskStatus;
import io.project.dto.taskStatusDto.TaskStatusCreateDTO;
import io.project.dto.taskStatusDto.TaskStatusDTO;
import io.project.dto.taskStatusDto.TaskStatusUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {
    public abstract TaskStatus map(TaskStatusCreateDTO model);
    public abstract TaskStatusDTO map(TaskStatus model);
    public abstract void update(TaskStatusUpdateDTO update, @MappingTarget TaskStatus destination);
}

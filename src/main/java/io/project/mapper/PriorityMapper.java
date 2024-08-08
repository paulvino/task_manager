package io.project.mapper;

import io.project.dto.priorityDto.PriorityCreateDTO;
import io.project.dto.priorityDto.PriorityDTO;
import io.project.dto.priorityDto.PriorityUpdateDTO;
import io.project.model.Priority;
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
public abstract class PriorityMapper {
    public abstract Priority map(PriorityCreateDTO model);
    public abstract PriorityDTO map(Priority model);
    public abstract void update(PriorityUpdateDTO update, @MappingTarget Priority destination);
}

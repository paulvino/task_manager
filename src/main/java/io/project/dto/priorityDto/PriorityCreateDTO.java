package io.project.dto.priorityDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriorityCreateDTO {
    @NotBlank
    private String name;
}

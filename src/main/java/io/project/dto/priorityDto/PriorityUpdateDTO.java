package io.project.dto.priorityDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class PriorityUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;
}

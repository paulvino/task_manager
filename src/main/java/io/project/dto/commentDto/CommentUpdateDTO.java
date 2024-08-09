package io.project.dto.commentDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class CommentUpdateDTO {
    @NotBlank
    private JsonNullable<String> commentText;
}

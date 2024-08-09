package io.project.dto.commentDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    @NotBlank
    private String commentText;
    private Long taskId;
}

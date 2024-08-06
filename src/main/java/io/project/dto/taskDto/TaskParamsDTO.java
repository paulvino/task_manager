package io.project.dto.taskDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskParamsDTO {

    private String titleCont;

    private Long assigneeId;

    private Long authorId;

    private String status;
}

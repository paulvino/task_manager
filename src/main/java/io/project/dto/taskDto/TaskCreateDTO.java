package io.project.dto.taskDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank
    private String title;

    @Column(unique = true)
    private Long index;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("priority_id")
    private Long priorityId;

    @NotNull
    private String status;

    @JsonProperty
    private List<Long> commentIds;
}

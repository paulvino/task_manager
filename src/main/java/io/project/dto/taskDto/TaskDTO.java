package io.project.dto.taskDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private Long index;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("priority_id")
    private Long priorityId;

    private String title;

    private String content;

    private String status;

    @JsonProperty("comment_ids")
    private List<Long> commentIds;
}

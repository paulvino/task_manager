package io.project.util;

import io.project.dto.taskDto.TaskParamsDTO;
import io.project.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskUtils {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withTaskStatus(params.getStatus()));
    }

    private Specification<Task> withTitleCont(String data) {
        return (root, query, criteriaBuilder) -> data == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("titleCont")), "%" + data + "%");
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> assigneeId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withTaskStatus(String slug) {
        return (root, query, criteriaBuilder) -> slug == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("taskStatus").get("slug"), slug);
    }

    private Specification<Task> withPriorityId(Long priorityId) {
        return (root, query, criteriaBuilder) -> priorityId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("priority").get("id"), priorityId);
    }

    private Specification<Task> withCommentId(Long commentId) {
        return (root, query, criteriaBuilder) -> commentId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("comments").get("id"), commentId);
    }
}


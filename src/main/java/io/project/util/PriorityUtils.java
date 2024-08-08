package io.project.util;

import io.project.model.Priority;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PriorityUtils {

    @Bean
    public List<Priority> getDefaultPriorities() {
        var highPriority = createPriority("high");
        var mediumPriority = createPriority("medium");
        var lowPriority = createPriority("low");

        return List.of(
                highPriority,
                mediumPriority,
                lowPriority
        );
    }

    private Priority createPriority(String name) {
        var priority = new Priority();
        priority.setName(name);
        return priority;
    }
}

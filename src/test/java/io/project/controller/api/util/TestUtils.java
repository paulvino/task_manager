package io.project.controller.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import io.project.controller.api.AuthenticationController;
import io.project.model.Priority;
import io.project.model.Task;
import io.project.model.TaskStatus;
import io.project.model.User;
import io.project.repository.PriorityRepository;
import io.project.repository.TaskRepository;
import io.project.repository.TaskStatusRepository;
import io.project.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@Getter
@Component
public class TestUtils {
    private Model<User> userModel;
    private Model<Task> taskModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Priority> priorityModel;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Autowired
    private Faker faker;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PriorityRepository priorityRepository;
    @Autowired
    private AuthenticationController authenticationController;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getAssignedTasks))
                .ignore(Select.field(User::getCreatedTasks))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug() + faker.internet().slug())
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word() + faker.lorem().word())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getAuthor))
                .ignore(Select.field(Task::getTaskStatus))
                .supply(Select.field(Task::getName), () -> faker.lorem().word() + faker.lorem().sentence())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
                .toModel();

        priorityModel = Instancio.of(Priority.class)
                .ignore(Select.field(Priority::getId))
                .ignore(Select.field(Priority::getCreatedAt))
                .supply(Select.field(Priority::getName), () -> faker.lorem().word() + faker.lorem().sentence())
                .supply(Select.field(Priority::getTasks), () -> new ArrayList<Task>())
                .toModel();
    }

    @Bean
    public void clean() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        priorityRepository.deleteAll();
    }

    @Bean
    public Task getTestTask() {
        var testTask = Instancio.of(getTaskModel())
                .create();

        var testUser = Instancio.of(getUserModel())
                .create();

        jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        userRepository.save(testUser);
        testTask.setAssignee(testUser);
        testTask.setAuthor(testUser);

        var testTaskStatus = Instancio.of(getTaskStatusModel())
                .create();

        taskStatusRepository.save(testTaskStatus);
        testTask.setTaskStatus(testTaskStatus);

        var testPriority = Instancio.of(getPriorityModel())
                .create();
        priorityRepository.save(testPriority);
        testTask.setPriority(testPriority);
        testPriority.getTasks().add(testTask);

        return testTask;
    }
}

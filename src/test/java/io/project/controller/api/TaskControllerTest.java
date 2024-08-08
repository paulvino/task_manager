package io.project.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import net.datafaker.Faker;
import net.javacrumbs.jsonunit.core.Option;

import io.project.model.Task;
import io.project.util.UserUtils;
import io.project.repository.TaskRepository;
import io.project.controller.api.util.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private ObjectMapper om;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private Task testTaskCreate;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(UserUtils.ADMIN_EMAIL));
        testTask = testUtils.getTestTask();
        taskRepository.save(testTask);

        testTaskCreate = testUtils.getTestTask();
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var tasks = om.readValue(body, new TypeReference<List<Task>>() {
        });
        var expected = taskRepository.findAll();

        assertThat(tasks).containsAll(expected);
    }

    @Test
    public void testIndexWithFilter() throws Exception {
        var titleCont = testTask.getName();
        var assigneeId = testTask.getAssignee().getId();
        var authorId = testTask.getAuthor().getId();
        var status = testTask.getTaskStatus().getSlug();
        var priorityId = testTask.getPriority().getId();

        var request = get("/api/tasks"
                + "?"
                + "name=" + titleCont
                + "&assigneeId=" + assigneeId
                + "&authorId=" + authorId
                + "&priorityId=" + priorityId
                + "&status=" + status)
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var data = new HashMap<>();
        data.put("assignee_id", assigneeId);
        data.put("author_id", authorId);
        data.put("content", testTask.getDescription());
        data.put("createdAt", testTask.getCreatedAt().format(TestUtils.FORMATTER));
        data.put("id", testTask.getId());
        data.put("priority_id", priorityId);
        data.put("status", status);
        data.put("title", titleCont);

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).when(Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_FIELDS)
                .isArray()
                .contains(om.writeValueAsString(data));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                jsonAssert -> jsonAssert.node("id").isEqualTo(testTask.getId()),
                jsonAssert -> jsonAssert.node("title").isEqualTo(testTask.getName()),
                jsonAssert -> jsonAssert.node("content").isEqualTo(testTask.getDescription()),
                jsonAssert -> jsonAssert.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                jsonAssert -> jsonAssert.node("author_id").isEqualTo(testTask.getAuthor().getId()),
                jsonAssert -> jsonAssert.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                jsonAssert -> jsonAssert.node("createdAt")
                        .isEqualTo(testTask.getCreatedAt().format(TestUtils.FORMATTER))
        );

        var receivedTask = om.readValue(body, Task.class);
        assertThat(receivedTask).isEqualTo(testTask);
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("title", testTaskCreate.getName());
        data.put("content", testTaskCreate.getDescription());
        data.put("status", testTaskCreate.getTaskStatus().getSlug());
        data.put("assignee_id", testTaskCreate.getAssignee().getId());
        data.put("author", testTaskCreate.getAuthor().getEmail());

        var token = jwt().jwt(builder -> builder.subject(testTaskCreate.getAuthor().getEmail()));

        var tasksCount = taskRepository.count();

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(taskRepository.count()).isEqualTo(tasksCount + 1);

        var task = taskRepository.findByName(testTaskCreate.getName()).get();

        assertNotNull(task);
        assertThat(taskRepository.findByName(testTaskCreate.getName())).isPresent();

        assertThat(task.getName()).isEqualTo(testTaskCreate.getName());
        assertThat(task.getDescription()).isEqualTo(testTaskCreate.getDescription());
        assertThat(task.getAssignee()).isEqualTo(testTaskCreate.getAssignee());
        assertThat(task.getAuthor()).isEqualTo(testTaskCreate.getAuthor());
        assertThat(task.getTaskStatus()).isEqualTo(testTaskCreate.getTaskStatus());
    }

    @Test
    public void testUpdate() throws Exception {
        var oldDescription = testTask.getDescription();
        var newDescription = faker.lorem().sentence();

        var data = new HashMap<>();
        data.put("content", newDescription);

        var tasksCount = taskRepository.count();

        token = jwt().jwt(builder -> builder.subject(oldDescription));

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(taskRepository.count()).isEqualTo(tasksCount);

        var task = taskRepository.findByName(testTask.getName()).get();

        assertThat(task.getDescription()).isEqualTo(newDescription);
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getAssignee()).isEqualTo(testTask.getAssignee());

        assertThat(taskRepository.findByDescription(oldDescription)).isEmpty();
        assertThat(taskRepository.findByDescription(newDescription)).get().isEqualTo(task);
    }

    @Test
    public void testDestroy() throws Exception {
        var tasksCount = taskRepository.count();

        token = jwt().jwt(builder -> builder.subject(testTask.getName()));

        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.count()).isEqualTo(tasksCount - 1);
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}

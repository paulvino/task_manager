package io.project.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.datafaker.Faker;
import org.instancio.Instancio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import io.project.controller.api.util.TestUtils;
import io.project.model.TaskStatus;
import io.project.repository.TaskStatusRepository;
import io.project.util.UserUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(UserUtils.ADMIN_EMAIL));
        testTaskStatus = Instancio.of(testUtils.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var taskStatuses = om.readValue(body, new TypeReference<List<TaskStatus>>() { });
        var expected = taskStatusRepository.findAll();

        assertThat(taskStatuses).containsAll(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testTaskStatus.getId()),
                json -> json.node("name").isEqualTo(testTaskStatus.getName()),
                json -> json.node("slug").isEqualTo(testTaskStatus.getSlug()),
                json -> json.node("createdAt").isEqualTo(testTaskStatus.getCreatedAt().format(TestUtils.FORMATTER))
        );

        var receivedTaskStatus = om.readValue(body, TaskStatus.class);
        assertThat(receivedTaskStatus).isEqualTo(testTaskStatus);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(testUtils.getTaskStatusModel())
                .create();

        var taskStatusesCount = taskStatusRepository.count();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.count()).isEqualTo(taskStatusesCount + 1);

        var addedTaskStatus = taskStatusRepository.findByName(data.getName()).get();

        assertNotNull(addedTaskStatus);
        assertThat(taskStatusRepository.findByName(testTaskStatus.getName())).isPresent();

        assertThat(addedTaskStatus.getName()).isEqualTo(data.getName());
        assertThat(addedTaskStatus.getSlug()).isEqualTo(data.getSlug());
    }

    @Test
    public void testCreateTaskStatusWithoutAuthorization() throws Exception {
        var data = Instancio.of(testUtils.getTaskStatusModel())
                .create();

        var taskStatusesCount = taskStatusRepository.count();

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertThat(taskStatusRepository.count()).isEqualTo(taskStatusesCount);

    }

    @Test
    public void testUpdate() throws Exception {
        var oldSlug = testTaskStatus.getSlug();
        var newSlug = faker.internet().slug();

        var data = new HashMap<>();
        data.put("slug", newSlug);

        var taskStatusesCount = taskStatusRepository.count();

        token = jwt().jwt(builder -> builder.subject(oldSlug));

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.count()).isEqualTo(taskStatusesCount);

        var taskStatus = taskStatusRepository.findByName(testTaskStatus.getName()).get();

        assertThat(taskStatus.getSlug()).isEqualTo(newSlug);
        assertThat(taskStatusRepository.findBySlug(oldSlug)).isEmpty();
        assertThat(taskStatusRepository.findBySlug(newSlug)).get().isEqualTo(taskStatus);
    }

    @Test
    public void testUpdateTaskStatusWithoutAuthorization() throws Exception {
        var oldSlug = testTaskStatus.getSlug();
        var newSlug = faker.internet().slug();

        var data = new HashMap<>();
        data.put("slug", newSlug);

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        assertThat(taskStatusRepository.findBySlug(oldSlug)).isPresent();
        assertThat(taskStatusRepository.findBySlug(newSlug)).isEmpty();
    }

    @Test
    public void testDestroy() throws Exception {
        var taskStatusesCount = taskStatusRepository.count();

        token = jwt().jwt(builder -> builder.subject(testTaskStatus.getName()));

        mockMvc.perform(delete("/api/task_statuses/" + testTaskStatus.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.count()).isEqualTo(taskStatusesCount - 1);
        assertThat(taskStatusRepository.findById(testTaskStatus.getId())).isEmpty();
    }

    @Test
    public void testDestroyTaskStatusWithoutAuthorization() throws Exception {
        var taskStatusesCount = taskStatusRepository.count();

        mockMvc.perform(delete("/api/task_statuses/" + testTaskStatus.getId()))
                .andExpect(status().isUnauthorized());

        assertThat(taskStatusRepository.count()).isEqualTo(taskStatusesCount);
    }
}

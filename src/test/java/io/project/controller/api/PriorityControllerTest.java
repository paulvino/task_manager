package io.project.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;

import io.project.controller.api.util.TestUtils;
import io.project.model.Priority;
import io.project.repository.PriorityRepository;
import io.project.repository.TaskRepository;
import io.project.util.UserUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class PriorityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;

    private Priority testPriority;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(UserUtils.ADMIN_EMAIL));
        testPriority = Instancio.of(testUtils.getPriorityModel())
                .create();
        priorityRepository.save(testPriority);
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/priorities")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var priorities = om.readValue(body, new TypeReference<List<Priority>>() { });
        var expected = priorityRepository.findAll();

        assertThat(priorities.containsAll(expected));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/priorities/" + testPriority.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testPriority.getId()),
                json -> json.node("name").isEqualTo(testPriority.getName()),
                json -> json.node("createdAt").isEqualTo(testPriority.getCreatedAt().format(testUtils.FORMATTER))
        );

        var receivedPriority = om.readValue(body, Priority.class);
        assertThat(receivedPriority).isEqualTo(testPriority);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(testUtils.getPriorityModel())
                .create();

        var prioritiesCount = priorityRepository.count();

        var request = post("/api/priorities")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(priorityRepository.count()).isEqualTo(prioritiesCount + 1);

        var addedPriority = priorityRepository.findByName(data.getName()).get();

        assertNotNull(addedPriority);
        assertThat(priorityRepository.findByName(testPriority.getName())).isPresent();
        assertThat(addedPriority.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var oldName = testPriority.getName();
        var newName = faker.lorem().word();

        var data = new HashMap<>();
        data.put("name", newName);

        var prioritiesCount = priorityRepository.count();

        var request = put("/api/priorities/" + testPriority.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(priorityRepository.count()).isEqualTo(prioritiesCount);

        var priority = priorityRepository.findById(testPriority.getId()).get();

        assertThat(priority.getName()).isEqualTo(newName);
        assertThat(priorityRepository.findByName(oldName)).isEmpty();
    }

    @Test
    public void testDestroy() throws Exception {
        var prioritiesCount = priorityRepository.count();

        token = jwt().jwt(builder -> builder.subject(testPriority.getName()));

        mockMvc.perform(delete("/api/priorities/" + testPriority.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(priorityRepository.count()).isEqualTo(prioritiesCount - 1);
        assertThat(priorityRepository.findById(testPriority.getId())).isEmpty();
    }

    @Test
    public void testDestroyPriorityWhichIsUsing() throws Exception {
        var taskForTest = testUtils.getTestTask();
        taskRepository.save(taskForTest);

        var priorityForTest = taskForTest.getPriority();

        mockMvc.perform(delete("/api/priorities/" + priorityForTest.getId()).with(token))
                .andExpect(status().isInternalServerError());

        assertThat(priorityRepository.findById(priorityForTest.getId())).isPresent();
    }
}

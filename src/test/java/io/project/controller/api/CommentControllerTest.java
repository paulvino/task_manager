package io.project.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.project.controller.api.util.TestUtils;
import io.project.model.Comment;
import io.project.repository.CommentRepository;
import io.project.util.UserUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    private JwtRequestPostProcessor token;

    private Comment testComment;


    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(UserUtils.ADMIN_EMAIL));
        testComment = Instancio.of(testUtils.getCommentModel())
                .create();
        commentRepository.save(testComment);
    }

    @AfterEach
    public void clean() {
        testUtils.clean();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/comments")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var comments = om.readValue(body, new TypeReference<List<Comment>>() { });
        var expected = commentRepository.findAll();

        assertThat(comments.containsAll(expected));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/comments/" + testComment.getId())
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testComment.getId()),
                json -> json.node("commentText").isEqualTo(testComment.getCommentText()),
                json -> json.node("createdAt").isEqualTo(testComment.getCreatedAt().format(testUtils.FORMATTER))
        );

        var receivedComment = om.readValue(body, Comment.class);
        assertThat(receivedComment).isEqualTo(testComment);
    }

    @Test
    public void testDestroy() throws Exception {
        var commentsCount = commentRepository.count();

        token = jwt().jwt(builder -> builder.subject(testComment.getCommentText()));

        mockMvc.perform(delete("/api/comments/" + testComment.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.count()).isEqualTo(commentsCount - 1);
        assertThat(commentRepository.findById(testComment.getId())).isEmpty();
    }
}

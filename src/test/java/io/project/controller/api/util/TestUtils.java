package io.project.controller.api.util;

import io.project.model.User;
import io.project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Getter
@Component
public class TestUtils {

    private Model<User> userModel;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Autowired
    private Faker faker;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .toModel();
    }

    @Bean
    public void clean() {
        userRepository.deleteAll();
    }
}

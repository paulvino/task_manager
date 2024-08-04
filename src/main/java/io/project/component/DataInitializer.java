package io.project.component;

import io.project.service.CustomUserDetailsService;
import io.project.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CustomUserDetailsService userService;

    private final UserUtils userUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var admin = userUtils.getAdmin();
        userService.createUser(admin);
    }
}

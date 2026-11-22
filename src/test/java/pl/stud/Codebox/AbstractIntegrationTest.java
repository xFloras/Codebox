package pl.stud.Codebox;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import pl.stud.Codebox.helpers.AuthHelpers;
import pl.stud.Codebox.helpers.ProblemHelpers;
import pl.stud.Codebox.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Container
    @ServiceConnection
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    static {
        postgreSQLContainer.start();
        rabbitMQContainer.start();
    }

    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper json;
    @Autowired protected UserRepository userRepository;

    protected AuthHelpers auth;
    protected ProblemHelpers problems;

    @BeforeEach
    void initHelpers() {
        auth = new AuthHelpers(mvc, json, userRepository);
        problems = new ProblemHelpers(mvc, json);
    }


}

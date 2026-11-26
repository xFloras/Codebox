package pl.stud.Codebox;

import org.junit.jupiter.api.Test;
import pl.stud.Codebox.dto.UserLoginRequest;
import pl.stud.Codebox.dto.UserLoginResponse;
import pl.stud.Codebox.dto.UserRegisterRequest;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthFlowTest extends AbstractIntegrationTest {

    @Test
    void signupThenLoginReturnsToken() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String username = "user_" + suffix;
        String email = username + "@example.com";
        String password = "Secret123!";

        mvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json.writeValueAsString(new UserRegisterRequest(username, email, password))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));

        String loginBody = mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json.writeValueAsString(new UserLoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserLoginResponse login = json.readValue(loginBody, UserLoginResponse.class);
        assertThat(login.token()).isNotBlank();
        assertThat(login.token().split("\\.")).hasSize(3);
        assertThat(login.expiresIn()).isPositive();
    }

    @Test
    void duplicateUsernameIsConflict() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String username = "user_" + suffix;
        String email = username + "@example.com";
        String password = "Secret123!";
        String body = json.writeValueAsString(new UserRegisterRequest(username, email, password));
        mvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void wrongPasswordIsUnauthorized() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String username = "user_" + suffix;
        String email = username + "@example.com";
        String password = "Secret123!";
        String wrongPassword = "WrongPassword!";
        mvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json.writeValueAsString(new UserRegisterRequest(username, email, password))))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(json.writeValueAsString(new UserLoginRequest(username, wrongPassword))))
                .andExpect(status().is4xxClientError());
    }
}

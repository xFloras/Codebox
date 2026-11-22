package pl.stud.Codebox.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.stud.Codebox.AbstractIntegrationTest;
import pl.stud.Codebox.dto.UserLoginRequest;
import pl.stud.Codebox.dto.UserLoginResponse;
import pl.stud.Codebox.dto.UserRegisterRequest;
import pl.stud.Codebox.models.Role;
import pl.stud.Codebox.models.User;
import pl.stud.Codebox.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class AuthHelpers {

    private final MockMvc mvc;
    private final ObjectMapper json;
    private final UserRepository userRepository;

    private static final String PASSWORD = "Password123!";

    public String userToken() throws Exception {
        String username = signUp();
        return login(username, PASSWORD);
    }

    public String adminToken() throws Exception {
        String username = signUp();
        User admin = userRepository.findByUsername(username).orElseThrow();
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        return login(username, PASSWORD);
    }

    public String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UserLoginRequest(username, password))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readValue(body, UserLoginResponse.class).token();
    }

    private String signUp() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        mvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UserRegisterRequest(username, username + "@test.com", PASSWORD))))
                .andExpect(status().isCreated());
        return username;
    }
}

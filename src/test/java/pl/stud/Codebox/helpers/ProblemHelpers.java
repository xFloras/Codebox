package pl.stud.Codebox.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.stud.Codebox.dto.ProblemRequest;
import pl.stud.Codebox.dto.ProblemResponse;
import pl.stud.Codebox.dto.TestCaseRequest;
import pl.stud.Codebox.dto.TestCaseResponse;
import pl.stud.Codebox.models.Difficulty;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class ProblemHelpers {
    private final MockMvc mvc;
    private final ObjectMapper json;

    public ProblemResponse createProblem(String adminToken, Integer timeLimit, Integer memoryLimit) throws Exception {
        String slug = "problem-" + randomLetters(8);
        String body = mvc.perform(post("/api/problems")
                        .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new ProblemRequest(slug, "Test problem",
                        "This is a test problem", Difficulty.EASY,
                        timeLimit, memoryLimit))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readValue(body, ProblemResponse.class);
    }

    public TestCaseResponse addTestCase(String adminToken, String slug, String input, String expectedOutput, Boolean sample) throws Exception {
        String body = mvc.perform(post("/api/problems/{slug}/test-cases", slug)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new TestCaseRequest(input, expectedOutput, sample))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return json.readValue(body, TestCaseResponse.class);
    }

    private static String randomLetters(int length) {
        var n = java.util.concurrent.ThreadLocalRandom.current();
        var sb = new StringBuilder();
        for(int i=0; i<length; i++) {
            sb.append((char) ('a' + n.nextInt(26)));
        }
        return sb.toString();
    }



}
